package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.assertTrue

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GwtPluginTest {

    @Test
    public void gwtCompileTask() {
        Project rootProject = ProjectBuilder.builder().build()

        rootProject.apply plugin: 'cat-gwt'

        assertTrue(rootProject.tasks.findByName('compileGwt') instanceof CompileGwtTask)
        assertTrue(rootProject.tasks.findByName('cleanGwt') instanceof CleanGwtTask)
        assertTrue(rootProject.tasks.findByName('generateLaunchConfig') instanceof EclipseLaunchConfigGwtTask)
    }

}