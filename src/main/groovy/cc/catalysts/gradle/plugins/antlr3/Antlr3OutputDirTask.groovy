package cc.catalysts.gradle.plugins.antlr3

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class Antlr3OutputDirTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @TaskAction
    def createAntlr3OutputDir() {
        File f = new File(project.projectDir.absolutePath, project.antlr3.destinationDir as String);
        if (!f.exists()) {
            log.lifecycle "Creating '${f.getPath()}'"
            if (!f.mkdirs()) {
                log.failure "Could not create directory '${f.getPath()}'", true
            }
        }
    }
}