package cc.catalysts.gradle.plugins.hibernate

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class HibernateOutputDirTask extends DefaultTask {
    @TaskAction
    def createHibernateOutputDir() {
        File f = new File(project.projectDir.absolutePath, project.hibernate.destinationDir);
        if (!f.exists()){
            println "     cat-hibernate: Creating '" + f.getPath() + "'"
           if (!f.mkdirs()){
                println "          Error in creating directory"
            }
        }
    }
}