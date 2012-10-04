package cc.catalysts.gradle.plugins.less

import com.asual.lesscss.LessEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class LessTask extends DefaultTask {

    @Input
    FileTree sourceFiles

    @Input
    File outputDirectory

    @Input
    @Optional
    Boolean compress = true

    @TaskAction
    void compile() {
        LessEngine engine = new LessEngine()

        sourceFiles.each { File sourceFile ->
            File outputFile = new File(outputDirectory, sourceFile.name.replace('.less', '.css'))
            engine.compile(sourceFile, outputFile, compress)
        }
    }
}
