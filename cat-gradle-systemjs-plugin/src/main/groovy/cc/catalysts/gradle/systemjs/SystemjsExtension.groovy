package cc.catalysts.gradle.systemjs

import cc.catalysts.gradle.npm.AbstractNpmAwareExtension
import org.gradle.api.Project

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class SystemjsExtension extends AbstractNpmAwareExtension {
    File srcDir
    /**
     * glob pattern
     */
    String includePath = "**/*.js"
    String bundlePath
    Map<String, String> npmDependencies = [
            'command-line-args': '2.1.4',
            'systemjs-builder' : '0.15.3'
    ]

    SystemjsExtension(Project project) {
        super(project, 'cat-systemjs')
        srcDir = new File(project.projectDir, 'src/main/resources')
        bundlePath = "META-INF/resources/webjars/${project.name}/${project.version}"
    }

    File getBundleLocation() {
        return new File(destinationDir, bundlePath)
    }

    static SystemjsExtension get(Project project) {
        return project.extensions.findByType(SystemjsExtension)
    }
}
