package cc.catalysts.gradle.systemjs

import org.gradle.api.Project

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class SystemjsExtension {
    File srcDir
    File destinationDir
    String includePath = "**${File.separator}*.js"
    String bundlePath

    SystemjsExtension(Project project) {
        srcDir = new File(project.projectDir, 'src/main/resources')
//        destinationDir = new File(project.buildDir, 'generated-resources/systemjs-bundle')
        bundlePath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"
        destinationDir = new File(project.buildDir, "generated-resources/cat-systemjs")
    }

    File getBundleLocation() {
        return new File(destinationDir, bundlePath)
    }
}
