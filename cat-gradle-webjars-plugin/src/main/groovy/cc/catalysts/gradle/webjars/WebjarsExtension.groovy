package cc.catalysts.gradle.webjars

import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
@Incubating
class WebjarsExtension {
    String packageName = 'cc.catalysts.gradle'
    File destinationDir

    Closure webjarFilter = { ResolvedArtifact it ->
        return it.moduleVersion.id.group?.startsWith('org.webjars')
    }

    WebjarsExtension(Project project) {
        destinationDir = new File(project.buildDir, "generated-sources/cat-webjars")
    }

    static WebjarsExtension get(Project project) {
        return project.extensions.findByType(WebjarsExtension)
    }
}
