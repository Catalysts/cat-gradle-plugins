package cc.catalysts.gradle.plugins.deploy

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class DeployPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.convention.plugins.deployTargets = new DeployExtension(project.container(DeployTarget))
        project.task('deploy', type: DeployTask)
    }
}