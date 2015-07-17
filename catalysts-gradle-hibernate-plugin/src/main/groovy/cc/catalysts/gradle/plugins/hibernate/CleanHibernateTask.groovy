package cc.catalysts.gradle.plugins.hibernate

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanHibernateTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @TaskAction
    def cleanHibernateTargetDir() {
        File f = new File(project.projectDir.absolutePath, project.hibernate.destinationDir as String);
        if (f.exists()) {
            log.lifecycle "Deleting '${f.getPath()}'"
            if (!f.deleteDir()) {
                log.failure "Could not delete directory '${f.getPath()}'", true
            }
        }
    }
}