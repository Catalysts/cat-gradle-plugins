package cc.catalysts.gradle.plugins.codegen.groovy

import cc.catalysts.gradle.plugins.codegen.CodegenTask
import cc.catalysts.gradle.plugins.codegen.java.CodegenJavaTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenGroovyPluginTest {

    @Test
    public void codegenTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-codegen-groovy'

        assertTrue(rootProject.tasks.codegenGroovyBuild instanceof CodegenGroovyTask)
        assertTrue(rootProject.tasks.codegen instanceof CodegenTask)
    }
}