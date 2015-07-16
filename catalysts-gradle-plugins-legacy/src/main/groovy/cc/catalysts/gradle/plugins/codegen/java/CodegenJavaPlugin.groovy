package cc.catalysts.gradle.plugins.codegen.java

import cc.catalysts.gradle.plugins.codegen.CodegenTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaPlugin implements Plugin<Project> {
    CodegenJavaExtension codegenJavaExtension

    void apply(Project project) {
        codegenJavaExtension = project.extensions.create("codegenjava", CodegenJavaExtension)

        project.apply plugin: 'java'

        if (project.tasks.findByPath('codegen') == null) {
            project.task('codegen', type: CodegenTask)
        }

        project.task('codegenJavaBuild', type: CodegenJavaTask)

        project.afterEvaluate {
            project.sourceSets {
                main {
                    java {
                        srcDir codegenJavaExtension.destDir
                    }
                }
            }
        }
    }
}