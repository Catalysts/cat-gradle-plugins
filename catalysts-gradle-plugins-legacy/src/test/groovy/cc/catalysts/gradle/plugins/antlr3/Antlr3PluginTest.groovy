package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class Antlr3PluginTest {

    @Test
    public void generateGrammarSourceTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-antlr3'

        assertTrue(rootProject.tasks.cleanAntlr3 instanceof CleanAntlr3Task)
        assertTrue(rootProject.tasks.createAntlr3Out instanceof Antlr3OutputDirTask)
        assertTrue(rootProject.tasks.generateGrammarSource instanceof GenerateGrammarSourceTask)
    }
}