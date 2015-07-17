package cc.catalysts.gradle.plugins.gwt.gwtp

import cc.catalysts.gradle.plugins.gwt.CleanGwtTask
import cc.catalysts.gradle.plugins.gwt.CompileGwtTask
import cc.catalysts.gradle.plugins.gwt.EclipseLaunchConfigGwtTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import static org.junit.Assert.assertTrue
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GwtpPluginTest {

    @Test
    public void gwtpCompileTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-gwt'
        rootProject.apply plugin: 'cat-gwtp'

        assertTrue(rootProject.tasks.findByName('compileGwt') instanceof CompileGwtTask)
        assertTrue(rootProject.tasks.findByName('cleanGwt') instanceof CleanGwtTask)
        assertTrue(rootProject.tasks.findByName('generateLaunchConfig') instanceof EclipseLaunchConfigGwtTask)

        assertTrue(rootProject.tasks.findByName('cleanTarget') != null)
        assertTrue(rootProject.tasks.findByName('createTarget') != null)
    }

}