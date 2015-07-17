package cc.catalysts.gradle.plugins.codegen.groovy

import cc.catalysts.gradle.plugins.codegen.CodegenTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenGroovyPlugin implements Plugin<Project> {
    CodegenGroovyExtension extension

    void apply(Project project) {
        extension = project.extensions.create("codegengroovy", CodegenGroovyExtension)

        project.apply plugin: 'groovy'

        if (project.tasks.findByPath('codegen') == null) {
            project.task('codegen', type: CodegenTask)
        }

        project.task('codegenGroovyBuild', type: CodegenGroovyTask)

        project.afterEvaluate {
            project.sourceSets {
                main {
                    groovy {
                        srcDir extension.destDir
                    }
                }
            }
        }
    }
}