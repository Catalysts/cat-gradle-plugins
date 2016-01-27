package cc.catalysts.gradle.plugins.sass

import cc.catalysts.gradle.plugins.TestUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class SassPluginTest {


    //@Test
    public void compile() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'cat-sass'

        SassTask task = project.tasks.sass
        task.sourceFiles = project.fileTree(dir: getClass().getResource('/sass/input'), include: '*.s*ss', excludes: ['_imported.sass'])
        task.outputDirectory = TestUtils.getTempDirectory()

        task.compile()
        TestUtils.compareDirectories(task.outputDirectory, project.file(getClass().getResource('/sass/expectation')))
        task.outputDirectory.deleteDir()
    }

}
