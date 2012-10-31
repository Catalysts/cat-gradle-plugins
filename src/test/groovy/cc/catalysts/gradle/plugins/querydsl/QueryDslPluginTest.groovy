package cc.catalysts.gradle.plugins.querydsl

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class QueryDslPluginTest {

    @Test
    public void querydslTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-querydsl'

        assertTrue(rootProject.tasks.getByName('cleanTarget') != null)
        assertTrue(rootProject.tasks.getByName('createTarget') != null)
    }
}