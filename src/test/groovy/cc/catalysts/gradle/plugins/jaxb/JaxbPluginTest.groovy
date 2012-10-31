package cc.catalysts.gradle.plugins.jaxb

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class JaxbPluginTest {

    @Test
    public void jaxbTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-jaxb'

        rootProject.task('generateWsdl', type: GenerateWsdlTask)
        rootProject.task('generateJaxb', type: GenerateJaxbTask)

        assertTrue(rootProject.tasks.getByName('jaxb') instanceof JaxbTask)
        assertTrue(rootProject.tasks.getByName('generateWsdl') instanceof GenerateWsdlTask)
        assertTrue(rootProject.tasks.getByName('generateJaxb') instanceof GenerateJaxbTask)
    }
}