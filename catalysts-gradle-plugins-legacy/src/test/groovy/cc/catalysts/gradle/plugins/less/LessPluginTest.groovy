package cc.catalysts.gradle.plugins.less

import cc.catalysts.gradle.plugins.TestUtils
import cc.catalysts.gradle.plugins.less.LessTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class LessPluginTest {

    @Test
    public void compile() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'cat-less'

        LessTask task = project.tasks.less
        task.sourceFiles = project.fileTree(dir: getClass().getResource('/less/input'), include: '*.less', excludes: ['_imported.less'])
        task.outputDirectory = TestUtils.getTempDirectory()

        task.compile()
        TestUtils.compareDirectories(task.outputDirectory, project.file(getClass().getResource('/less/expectation')))
        task.outputDirectory.deleteDir()
    }

}
