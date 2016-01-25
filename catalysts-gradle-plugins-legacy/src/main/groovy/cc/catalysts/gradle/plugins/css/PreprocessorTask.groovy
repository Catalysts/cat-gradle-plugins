package cc.catalysts.gradle.plugins.css

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class PreprocessorTask extends DefaultTask {

    @Input
    FileTree sourceFiles

    @Input
    File outputDirectory

}
