package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanAntlr3Task extends DefaultTask {
    @TaskAction
    def cleanAntlr3() {
        File f = new File(project.antlr3.destinationDir);
        if (f.exists()){
            println "     Deleting '" + f.getPath() + "'"
            while(f.getParent() != null){
                f = new File(f.getParent())
            }
            if (!project.delete(f.getPath())){
                println "          Error in deleting directory"
            }
        }
    }
}