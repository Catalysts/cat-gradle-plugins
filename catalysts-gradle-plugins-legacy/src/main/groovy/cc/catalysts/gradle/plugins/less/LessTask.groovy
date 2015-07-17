package cc.catalysts.gradle.plugins.less

import cc.catalysts.gradle.plugins.css.PreprocessorTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.lesscss.LessCompiler

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class LessTask extends PreprocessorTask {

    @Input
    @Optional
    Boolean compress = true

    @TaskAction
    void compile() {
        LessCompiler lessCompiler = new LessCompiler()
        lessCompiler.setCompress(compress)

        sourceFiles.each { File sourceFile ->
            File outputFile = new File(outputDirectory, sourceFile.name.replaceAll(/\.less$/, ".css"))
            lessCompiler.compile(sourceFile, outputFile)
        }
    }
}
