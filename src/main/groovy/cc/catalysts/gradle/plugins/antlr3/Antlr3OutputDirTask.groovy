package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class Antlr3OutputDirTask extends DefaultTask {
    @TaskAction
    def generateGrammarSource() {
        File f = new File(project.antlr3.destinationDir);
        if (!f.exists()){
            println "     Creating '" + f.getPath() + "'"
           if (!f.mkdirs()){
                println "          Error in creating directory"
            }
        }
    }
}