package cc.catalysts.gradle.plugins.querydsl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanQueryDslTask extends DefaultTask {
    @TaskAction
    def cleanQueryDslTargetDir() {
        File f = new File(project.projectDir.absolutePath + project.querydsl.destinationDir);
        if (f.exists()){
            println "     cat-querydsl: Deleting '" + f.getPath() + "'"
            while(f.getParent() != null){
                f = new File(f.getParent())
            }
            if (!project.delete(f.getPath())){
                println "          Error in deleting directory"
            }
        }
    }
}