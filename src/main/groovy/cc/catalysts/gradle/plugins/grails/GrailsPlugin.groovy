package cc.catalysts.gradle.plugins.grails

import com.connorgarvey.gradlegrails.GrailsPlugin as GradleGrailsWrapperPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.sonar.SonarPlugin

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsPlugin implements Plugin<Project> {
    Project project

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
                overwrite: true,
                type: GrailsTestTask)

        testTask.convention.testResultsDir = project.file('target/test-reports')
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
        project.extensions.create('catgrails', GrailsExtension)

        // in case there is no rootProject, rootProject simply points to the current project
        if (project.plugins.hasPlugin(SonarPlugin) || project.rootProject.plugins.hasPlugin(SonarPlugin)) {
            project.sonar.project {
                language = 'grvy'
                testReportPath = project.file('target/test-reports')
                coberturaReportPath = project.file('target/test-reports/cobertura/coverage.xml')
                dynamicAnalysis = 'reuseReports'
            }
        }

        applyGroovy()
        applyGrailsWrapper()
        addTasks()
    }
}