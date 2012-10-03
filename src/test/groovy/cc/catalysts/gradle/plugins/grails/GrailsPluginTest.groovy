package cc.catalysts.gradle.plugins.grails

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.*

class GrailsPluginTest {
/*
    @Test(expected=InvalidUserDataException)
    public void exceptionIfApplicationNotSet() {
        Project rootProject = ProjectBuilder.builder().build()
        rootProject.apply plugin: 'cat-grails'
    }

    @Test(expected=InvalidUserDataException)
    public void exceptionIfGrailsVersionNotSet() {
        Project rootProject = ProjectBuilder.builder().build()
        Project subProject = ProjectBuilder.builder().withParent(rootProject).build()

        rootProject.ext.grails = new GrailsExtension()
        rootProject.grails.application = subProject

        rootProject.apply plugin: 'cat-grails'
    }

    @Test
    public void warTask() {
        Project rootProject = ProjectBuilder.builder().build()
        Project subProject = ProjectBuilder.builder().withParent(rootProject).build()

        rootProject.ext.grails = new GrailsExtension()
        rootProject.grails.application = subProject
        rootProject.grails.version = '2.1.0'

        rootProject.apply plugin: 'cat-grails'

        assertTrue(rootProject.tasks.war instanceof WarTask)
    }
*/
}