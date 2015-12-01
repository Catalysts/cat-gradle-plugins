package cc.catalysts.gradle.plugins.grails

import cc.catalysts.gradle.utils.TCLogger
import com.connorgarvey.gradlegrails.Grails
import com.connorgarvey.gradlegrails.GrailsPlugin as GradleGrailsWrapperPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.tasks.testing.DefaultTestTaskReports
import org.gradle.api.logging.Logging
import org.sonarqube.gradle.SonarQubePlugin

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
                    srcDirs 'grails-app', 'src/groovy', 'scripts'
                }
                java {
                    srcDir 'src/java'
                }
                resources {
                    srcDirs 'web-app', 'conf'
                }
            }
            test {
                groovy {
                    srcDirs 'test/unit', 'test/integration'
                }
            }
        }
    }

    private void applyGrailsWrapper() {
        project.apply plugin: GradleGrailsWrapperPlugin
        // Add additional configuration extensions
        Grails.metaClass.allowBuildCompile = false
        Grails.metaClass.allowBuildCompile {
            delegate.allowBuildCompile = it
        }
    }

    private Task addTestTask() {
        // Overwrite existing behaviour without creating a new task if possible
        Task testTask = project.tasks.findByName('test') ?: project.task('test')
        testTask.with {
            group = 'cat-grails'
            description = 'Tests the project'
            actions = []
        }
        testTask.convention.testResultsDir = project.file('target/test-reports')
        testTask.convention.reports = new DefaultTestTaskReports(testTask)
        testTask.convention.reports.junitXml.destination = project.file('target/test-reports')

        Task grailsTest = project.task('grailsTest', group: 'cat-grails',
            description: 'Run grails-test-app and generate coverage reports, if indicated by grailsCoverage property.')
        grailsTest.doFirst {
            if (project.hasProperty('grailsCoverage') && project.grailsCoverage) {
                log.debug("grails: test task will create coverage report xml")
                GrailsUtils.executeGrailsCommand(project, ["test-app", "-coverage", "-xml"], false)
            } else {
                GrailsUtils.executeGrailsCommand(project, ["test-app"], false)
            }
        }
        testTask.dependsOn(grailsTest)

        project.task('testCoverage',
                group: 'cat-grails',
                description: 'Tests the project and generates a coverage report',
                type: GrailsTestCoverageTask)

        return testTask
    }

    private Task addBuildTask() {
        // Overwrite existing behaviour without creating a new task if possible
        Task buildTask = project.tasks.findByName('build') ?: project.task('build')
        buildTask.with {
            group = 'cat-grails'
            description = 'Tests the project and (if it is an application) builds the war archive'
            actions = []
        }

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

        // Prevent compilation of source files outside of grails scope, as BuildConfig dependencies are not yet resolved
        project.afterEvaluate {
            if(!project.grails.allowBuildCompile) {
                ['compileJava', 'compileTestJava', 'compileGroovy', 'compileTestGroovy'].each { task ->
                    project.tasks.getByName(task).actions = []
                }
            }
        }

        // in case there is no rootProject, rootProject simply points to the current project
        if (project.plugins.hasPlugin(SonarQubePlugin) || project.rootProject.plugins.hasPlugin(SonarQubePlugin)) {
            log.debug("Adding standard grails groovy sonar-runner config")
            project.sonarqube.properties {
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