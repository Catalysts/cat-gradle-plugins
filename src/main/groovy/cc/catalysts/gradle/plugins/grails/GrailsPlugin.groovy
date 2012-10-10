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

        project.rootProject.afterEvaluate {
            if (project.rootProject.grails.version == null) {
                throw new InvalidUserDataException("Please specify the grails version in your build.gradle, e.g. grails { version = '2.1.0' }")
            }

            // in subproject here, project.grails = Extension from GradleGrailsWrapperPlugin
            project.grails {
                version project.rootProject.grails.version
            }
        }
    }

    void addBuildTasks() {
        Task warTask = project.task('war',
                group: 'cat-grails',
                description: 'Creates a war archive of the grails application',
                type: WarTask)

        // add empty build task on root project,
        // dependsOn warTask
        Task buildTask = project.task('build')
        buildTask.dependsOn(warTask)
    }

    void apply(Project project) {
        this.project = project
        Boolean isRoot = project.rootProject == project

        if (isRoot) {
            project.extensions.create('grails', GrailsExtension)

            project.subprojects {
                apply plugin: GrailsPlugin
            }

            addBuildTasks()
        }
        else {
            // for all subprojects

            if (project.plugins.hasPlugin(SonarPlugin)) {
                project.sonar.project.language = 'grvy'
            }

            applyGroovy()
            applyGrailsWrapper()

            // overwrite standard groovy build task
            // with empty task
            project.task('build', overwrite: true)

            // map test task to grails-test-app
            Task testTask = project.task('test', overwrite: true)
            testTask.dependsOn(project.tasks.'grails-test-app')
        }
    }
}