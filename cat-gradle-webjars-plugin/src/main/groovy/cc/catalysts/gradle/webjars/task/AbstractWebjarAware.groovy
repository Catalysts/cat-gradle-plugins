package cc.catalysts.gradle.webjars.task

import cc.catalysts.gradle.webjars.WebjarsExtension
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.Input

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
abstract class AbstractWebjarAware extends AbstractTask {

    Closure<Boolean> webjarFilter

    WebjarsExtension getConfig() {
        return WebjarsExtension.get(project);
    }

    Closure<Boolean> getWebjarFilter() {
        return webjarFilter?: config.webjarFilter
    };

    @Input
    Set<String> getWebjarDescriptors() {
        return getWebjars()
        .collect({ResolvedArtifact it ->
            ModuleVersionIdentifier id = it.moduleVersion.id
            return "${id.group}:${id.name}:${id.version}"
        }).toSet()
    }

    Set<ResolvedArtifact> getWebjars() {
        Set<ResolvedArtifact> webjars = []
        project.configurations.forEach({ Configuration configuration ->
            webjars.addAll(configuration
                    .resolvedConfiguration
                    .resolvedArtifacts
                    .findAll(getWebjarFilter()))
        })
        return webjars
    }
}
