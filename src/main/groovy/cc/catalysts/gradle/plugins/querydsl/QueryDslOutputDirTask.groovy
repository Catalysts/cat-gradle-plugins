package cc.catalysts.gradle.plugins.querydsl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class QueryDslOutputDirTask extends DefaultTask {
    @TaskAction
    def createQueryDslOutputDir() {
        File f = new File(project.projectDir.absolutePath + project.querydsl.destinationDir);
        if (!f.exists()){
            println "     cat-querydsl: Creating '" + f.getPath() + "'"
           if (!f.mkdirs()){
                println "          Error in creating directory"
            }
        }
    }
}