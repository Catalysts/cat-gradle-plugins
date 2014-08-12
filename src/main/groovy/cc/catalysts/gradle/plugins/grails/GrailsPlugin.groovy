package cc.catalysts.gradle.plugins.grails

import cc.catalysts.gradle.utils.TCLogger
import com.connorgarvey.gradlegrails.GrailsPlugin as GradleGrailsWrapperPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.tasks.testing.DefaultTestTaskReports
import org.gradle.api.logging.Logging
import org.gradle.api.sonar.runner.SonarRunnerPlugin

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsPlugin implements Plugin<Project> {
    Project project
    TCLogger log

    private void applyGroovy() {
        project.apply plugin: 'groovy'

        // source set definitions for the sonar plugin
        project.sourceSets {
            main {
                groovy {
                    srcDir 'grails-app'
                    srcDir 'src/groovy'
                    srcDir 'scripts'
                }
                resources {
                    srcDir 'web-app'
                    srcDir 'conf'
                }
            }
            test {
                groovy {
                    srcDir 'test/unit'
                    srcDir 'test/integration'
                }
            }
        }
    }

    private void applyGrailsWrapper() {
        project.apply plugin: GradleGrailsWrapperPlugin
    }

    private Task addTestTask() {
        Task testTask = project.task('test',
                group: 'cat-grails',
                description: 'Tests the project',
                overwrite: true) << {
            if (project.hasProperty('grailsCoverage') && project.grailsCoverage) {
                log.debug("grails: test task will create coverage report xml")
                GrailsUtils.executeGrailsCommand(project, ["test-app", "-coverage", "-xml"], false)
            } else {
                GrailsUtils.executeGrailsCommand(project, ["test-app"], false)
            }
        }
        testTask.convention.testResultsDir = project.file('target/test-reports')
        testTask.convention.reports = new DefaultTestTaskReports(testTask)
        testTask.convention.reports.junitXml.destination = project.file('target/test-reports')

        Task testCoverageTask = project.task('testCoverage',
                group: 'cat-grails',
                description: 'Tests the project and generates a coverage report',
                type: GrailsTestCoverageTask)

        return testTask
    }

    private Task addBuildTask() {
        // empty build task, depends on test (and if it's a grails app) on war
        Task buildTask = project.task('build',
                group: 'cat-grails',
                description: 'Tests the project and (if it is an application) builds the war archive',
                overwrite: true)
        return buildTask
    }

    private Task addWarTask() {
        Task warTask = project.task('war',
                group: 'cat-grails',
                description: 'Creates a war archive of the grails application')
        warTask.dependsOn(project.tasks.'grails-war')
        return warTask
    }

    private void addTasks() {
        // clean depends on grails-clean
        project.tasks.clean.dependsOn(project.tasks.'grails-clean')

        Task testTask = addTestTask()

        Task buildTask = addBuildTask()
        buildTask.dependsOn(testTask)

        if (GrailsUtils.isGrailsApplication(project)) {
            Task warTask = addWarTask()
            buildTask.dependsOn(warTask)
        }
    }

    public void apply(Project project) {
        this.project = project
        log = new TCLogger(project, Logging.getLogger(GrailsPlugin.class))

        applyGroovy()
        applyGrailsWrapper()
        addTasks()

        // in case there is no rootProject, rootProject simply points to the current project
        if (project.plugins.hasPlugin(SonarRunnerPlugin) || project.rootProject.plugins.hasPlugin(SonarRunnerPlugin)) {
            log.debug("Adding standard grails groovy sonar-runner config")
            project.sonarRunner.sonarProperties {
                property "sonar.language", "grvy"
                property "sonar.sources", "src/groovy, grails-app"
                property "sonar.sourceEncoding", "UTF-8"
                property "sonar.tests", "test/unit,test/integration"
                property "sonar.surefire.reportsPath", "target/test-reports"
                property "sonar.cobertura.reportPath", "target/test-reports/cobertura/coverage.xml"
                property "sonar.groovy.cobertura.reportPath", "target/test-reports/cobertura/coverage.xml"
                property "sonar.exclusions", "grails-app/migrations/**"
            }
        }
    }
}