package cc.catalysts.gradle.plugins.hibernate

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanHibernateTask extends DefaultTask {
    @TaskAction
    def cleanHibernateTargetDir() {
        File f = new File(project.projectDir.absolutePath + File.separatorChar + project.hibernate.destinationDir);
        if (f.exists()){
            println "     cat-hibernate: Deleting '" + f.getPath() + "'"
            if (!f.deleteDir()){
                println "          Error in deleting directory"
            }
        }
    }
}