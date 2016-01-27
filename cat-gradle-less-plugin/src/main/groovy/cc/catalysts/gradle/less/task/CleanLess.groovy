package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import org.gradle.api.DefaultTask
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
        LessExtension config = LessExtension.get(project)
        project.delete(config.destinationDir)
        project.delete(config.nodeModulesDir)
    }
}
