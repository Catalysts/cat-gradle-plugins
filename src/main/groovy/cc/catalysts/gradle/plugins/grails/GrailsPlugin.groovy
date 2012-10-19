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

    boolean isGrailsApplication() {
        // if the project doesn't contain a *GrailsPlugin.groovy file, it's a grails application
        project.fileTree(dir: project.projectDir, include: '*GrailsPlugin.groovy').isEmpty()
    }

    void addSubProjectTasks() {
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

        if (isGrailsApplication()) {
            Task warTask = project.task('war',
                    group: 'cat-grails',
                    description: 'Creates a war archive of the grails application')
            warTask.dependsOn(project.tasks.'grails-war')

            buildTask.dependsOn(warTask)
        }
    }

    void apply(Project project) {
        this.project = project
        Boolean isRoot = project.rootProject == project

        if (isRoot) {
            project.extensions.create('grails', GrailsExtension)

            project.subprojects {
                apply plugin: GrailsPlugin
            }
        }
        else {
            // for all subprojects

            if (project.plugins.hasPlugin(SonarPlugin)) {
                project.sonar.project.language = 'grvy'
            }

            applyGroovy()
            applyGrailsWrapper()
            addSubProjectTasks()
        }
    }
}