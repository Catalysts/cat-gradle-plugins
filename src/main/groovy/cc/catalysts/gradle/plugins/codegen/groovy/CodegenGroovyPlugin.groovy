package cc.catalysts.gradle.plugins.codegen.groovy

import cc.catalysts.gradle.plugins.codegen.CodegenTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenGroovyPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: 'groovy'

        if (project.tasks.findByPath('codegen') == null) {
            project.task('codegen', type: CodegenTask)
        }

        project.extensions.codegengroovy = new CodegenGroovyExtension()
        project.task('codegenGroovyBuild', type: CodegenGroovyTask)

        project.afterEvaluate {
            project.sourceSets {
                main {
                    groovy {
                        srcDir project.extensions.codegengroovy.destDir
                    }
                }
            }
        }
    }
}