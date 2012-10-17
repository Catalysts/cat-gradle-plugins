package cc.catalysts.gradle.plugins.deploy

import org.gradle.api.DomainObjectSet;
import org.gradle.api.Named
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.binaries.model.NativeDependencyCapableSourceSet;
import org.gradle.plugins.binaries.model.NativeDependencySet;

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class DeployTarget implements Named {
    String name
    ProjectInternal project
    String type

    String tomcatHost
    String tomcatService
    String webappWar
    String webappDir

    String uploadTarget
    String uploadDir

    String username
    String password

    public DeployTarget(String name) {
        super()
        this.name = name;
        this.project = project;
    }

}