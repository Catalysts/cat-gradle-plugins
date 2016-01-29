package cc.catalysts.gradle.less.task

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class CleanLess extends DefaultTask {
    CleanLess() {
        description = 'Cleans output and working directory of less compiler'
        group = 'Cat-Boot LESS'
    }

    @TaskAction
    void clean() {
        Task lessTask = project.tasks.less;
        project.delete(lessTask.outputs.files)
    }
}
