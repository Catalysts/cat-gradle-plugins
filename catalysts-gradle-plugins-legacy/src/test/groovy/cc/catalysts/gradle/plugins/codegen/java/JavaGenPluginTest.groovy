package cc.catalysts.gradle.plugins.codegen.java

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class JavaGenPluginTest {

    @Test
    public void javagenTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-javagen'

        rootProject.task('javaGen', type: JavaGenTask)

        assertTrue(rootProject.tasks.getByName('javaGen') instanceof JavaGenTask)
    }
}