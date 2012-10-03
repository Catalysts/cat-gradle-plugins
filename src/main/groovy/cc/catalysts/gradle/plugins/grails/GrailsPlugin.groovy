package cc.catalysts.gradle.plugins.grails

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.sonar.SonarPlugin
import com.connorgarvey.gradlegrails.GrailsPlugin as GradleGrailsWrapperPlugin

class GrailsPlugin implements Plugin<Project> {
    Project project

    void applyGroovy() {
        project.apply plugin: 'groovy'
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

    void addWarTask() {
        project.task('war',
                group: 'grails',
                description: 'Creates a war archive of the grails application',
                type: WarTask)
    }

    void apply(Project project) {
        this.project = project
        Boolean isRoot = project.rootProject == project

        if (isRoot) {
            project.extensions.create('grails', GrailsExtension)

            project.subprojects {
                apply plugin: GrailsPlugin
            }

            addWarTask()
        }
        else {
            if (project.plugins.hasPlugin(SonarPlugin)) {
                project.sonar.project.language = 'grvy'
            }

            applyGroovy()
            applyGrailsWrapper()
        }
    }
}