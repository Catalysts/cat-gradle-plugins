package cc.catalysts.gradle.plugins.grails

import com.connorgarvey.gradlegrails.GrailsPlugin as GradleGrailsWrapperPlugin
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.sonar.SonarPlugin
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsPlugin implements Plugin<Project> {
    Project project

    void applyGroovy() {
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

    void applyGrailsWrapper() {
        project.apply plugin: GradleGrailsWrapperPlugin
    }

    void addTasks() {
        // clean depends on grails-clean
        project.tasks.clean.dependsOn(project.tasks.'grails-clean')

        // map test task to grails-test-app
        Task testTask = project.task('test',
                group: 'cat-grails',
                description: 'Tests the project',
                overwrite: true)
        testTask.dependsOn(project.tasks.'grails-test-app')

        // empty build task, depends on test (and if it's a grails app) on war
        Task buildTask = project.task('build',
                group: 'cat-grails',
                description: 'Tests the project and (if it is an application) builds the war archive',
                overwrite: true)
        buildTask.dependsOn(testTask)

        if (GrailsUtils.isGrailsApplication(project)) {
            Task warTask = project.task('war',
                    group: 'cat-grails',
                    description: 'Creates a war archive of the grails application')
            warTask.dependsOn(project.tasks.'grails-war')

            buildTask.dependsOn(warTask)
        }
    }

    void apply(Project project) {
        this.project = project

        if (project.plugins.hasPlugin(SonarPlugin)) {
            project.sonar.project.language = 'grvy'
        }

        applyGroovy()
        applyGrailsWrapper()
        addTasks()
    }
}