package cc.catalysts.boot.gradle.systemjs

import org.gradle.api.Project

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class SystemjsExtension {
    File srcDir
    File destinationDir
    String includePath = "**${File.separator}*.js"

    SystemjsExtension(Project project) {
        srcDir = new File(project.projectDir, 'src/main/resources')
//        destinationDir = new File(project.buildDir, 'generated-resources/systemjs-bundle')
        destinationDir = new File(project.buildDir, "resources/main/META-INF/resources/webjars/${project.name}/${project.rootProject.version}")
    }
}
