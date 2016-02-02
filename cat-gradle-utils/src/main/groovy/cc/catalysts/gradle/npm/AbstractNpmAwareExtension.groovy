package cc.catalysts.gradle.npm

import org.gradle.api.Project

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
abstract class AbstractNpmAwareExtension {
    Map<String, String> npmDependencies = [:]
    File destinationDir
    File nodeModulesDir

    AbstractNpmAwareExtension(Project project, String name) {
        destinationDir = new File(project.buildDir, "generated-resources/cat-${name}")
        nodeModulesDir = new File(project.buildDir, "cat-gradle/${name}")
    }

    PackageJson getPackageJson() {
        return PackageJson
                .initPrivate()
                .addDependencies(npmDependencies)
    }

    File getPackageJsonFile() {
        new File(nodeModulesDir, 'package.json')
    }
}
