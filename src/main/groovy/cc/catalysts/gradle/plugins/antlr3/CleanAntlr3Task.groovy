package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanAntlr3Task extends DefaultTask {
    @TaskAction
    def cleanAntlr3() {
        File f = new File(project.projectDir.absolutePath, project.antlr3.destinationDir);
        if (f.exists()){
            println "     Deleting '" + f.getPath() + "'"
            if (!f.deleteDir()){
                println "          Error in deleting directory"
            }
        }
    }
}