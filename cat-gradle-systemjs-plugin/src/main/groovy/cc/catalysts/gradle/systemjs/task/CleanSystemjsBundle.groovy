package cc.catalysts.gradle.systemjs.task

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class CleanSystemjsBundle extends DefaultTask {
    CleanSystemjsBundle() {
        description = 'Cleans output directory of systemjs bundle'
        group = 'cat-boot'
    }

    @TaskAction
    void cleanSystemjsBundle() {
        Task systemJsBundleTask = project.tasks.systemJsBundle;
        project.delete(systemJsBundleTask.outputs.files)
    }
}
