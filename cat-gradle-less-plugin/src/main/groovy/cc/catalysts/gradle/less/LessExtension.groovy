package cc.catalysts.gradle.less

import org.gradle.api.Project

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class LessExtension {
    File srcDir
    String[] srcFiles
    File destinationDir
    String cssPath

    LessExtension(Project project) {
        srcDir = new File(project.projectDir, 'src/main/resources/less')
        srcFiles = ["${project.name}.less"]
        destinationDir = new File(project.buildDir, "generated-resources/cat-less")
        cssPath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"
    }

    static LessExtension get(Project project) {
        return project.extensions.getByType(LessExtension);
    }

    File getCssLocation() {
        return new File(destinationDir, cssPath)
    }

    File[] getCssFiles() {
        return srcFiles.collect { new File(cssLocation, it.replace('.less', '.css')) }
    }

    File[] getLessFiles() {
        return srcFiles.collect { new File(srcDir, it) }
    }
}
