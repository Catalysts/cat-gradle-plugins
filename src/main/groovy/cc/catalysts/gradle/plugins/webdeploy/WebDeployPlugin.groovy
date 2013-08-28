package cc.catalysts.gradle.plugins.webdeploy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class WebDeployPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("webdeploy", WebDeployExtension)

        /*project.tasks.addRule("Pattern: webdeploy<Revision>: Deploys the site at the specified revision") { String taskName ->
            if (taskName.startsWith("webdeploy")) {
                String deployRevision = taskName - 'webdeploy';

                Task deployTask = project.task(taskName,
                        group: 'cat-webdeploy',
                        description: 'Deploys revision ' + deployRevision, type: WebDeployTask)

                deployTask.configure {
                    revision deployRevision
                }
            }
        }*/

        Task deployLatestTask = project.task('webdeploy',
                group: 'cat-webdeploy',
                description: 'Deploys the latest version',
                type: WebDeployTask
        )
        //deployLatestTask.dependsOn 'webdeployLatest'
    }

}