package cc.catalysts.gradle.plugins.grails

import org.gradle.api.Project
import org.gradle.api.plugins.sonar.SonarPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsPluginTest {

    @Test
    public void warTask() {
        Project project = ProjectBuilder.builder().build()

        project.apply plugin: 'cat-grails'

        // throws exception if task not found
        project.tasks.getByName('war')
        project.tasks.getByName('build')
        project.tasks.getByName('test')
    }

    @Test
    public void sonarLanguageSetting() {
        Project project = ProjectBuilder.builder().build()

        project.apply plugin: SonarPlugin
        project.apply plugin: 'cat-grails'

        assertEquals('grvy', project.sonar.project.language)
    }

    @Test
    public void groovySourceSets() {
        Project project = ProjectBuilder.builder().build()

        project.apply plugin: 'cat-grails'

        // check if grails-app is in sourceSets for main/groovy
        assertEquals(1, project.sourceSets.main.groovy.srcDirs.count {
            it.name == 'grails-app'
        })

        // check if unit directory is in sourceSets for test/groovy
        assertEquals(1, project.sourceSets.test.groovy.srcDirs.count {
            it.name == 'unit'
        })
    }

}