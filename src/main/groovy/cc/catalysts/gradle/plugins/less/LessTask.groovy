package cc.catalysts.gradle.plugins.less

import cc.catalysts.gradle.plugins.css.PreprocessorTask
import com.asual.lesscss.LessEngine
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class LessTask extends PreprocessorTask {

    @Input
    @Optional
    Boolean compress = true

    @TaskAction
    void compile() {
        LessEngine engine = new LessEngine()

        sourceFiles.each { File sourceFile ->
            File outputFile = new File(outputDirectory, sourceFile.name.replaceAll(/\.less$/, ".css"))
            engine.compile(sourceFile, outputFile, compress)
        }
    }
}
