package cc.catalysts.gradle.plugins.deploy

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class DeployPluginTest {

    @Test
    public void deployTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-deploy'

        assertTrue(rootProject.tasks.deploy instanceof DeployTask)
    }
}