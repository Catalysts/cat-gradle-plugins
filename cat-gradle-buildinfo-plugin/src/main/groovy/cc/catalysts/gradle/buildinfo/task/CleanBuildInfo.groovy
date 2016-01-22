package cc.catalysts.gradle.buildinfo.task

import cc.catalysts.gradle.buildinfo.BuildInfoExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Klaus Lehner
 */
class CleanBuildInfo extends DefaultTask {

    @TaskAction
    void cleanBuildInfo() {
        BuildInfoExtension config = project.buildinfo;

        project.delete(config.destinationDir)
    }
}
