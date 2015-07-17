package cc.catalysts.gradle.plugins.codegen.java

import cc.catalysts.gradle.plugins.codegen.CodegenTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaPluginTest {

    @Test
    public void codegenTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-codegen-java'

        assertTrue(rootProject.tasks.codegenJavaBuild instanceof CodegenJavaTask)
        assertTrue(rootProject.tasks.codegen instanceof CodegenTask)
    }
}