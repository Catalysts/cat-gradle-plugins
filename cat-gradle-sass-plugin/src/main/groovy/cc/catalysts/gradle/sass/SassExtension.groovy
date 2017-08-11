package cc.catalysts.gradle.sass

import cc.catalysts.gradle.npm.AbstractNpmAwareExtension
import cc.catalysts.gradle.npm.PackageJson
import org.gradle.api.Project

class SassExtension extends AbstractNpmAwareExtension {
    File srcDir
    String[] srcFiles
    String cssPath
    List<String> additionalArguments = [ ]
    Map<String, String> npmDependencies = [
        'node-sass'     : '4.5.3',
    ]
    List<String> plugins;
    Map<String, String> pluginOptions = [:]

    Closure<String> cssFileName = {
        return it.replace('.sass', '.css').replace('.scss', '.css')
    }

    SassExtension(Project project) {
        super(project, 'sass')

        java.io.File sassDir = new File(project.projectDir, 'src/main/resources/sass')
        java.io.File scssDir = new File(project.projectDir, 'src/main/resources/scss')

        if(sassDir.exists() && sassDir.isDirectory()) {
            srcDir = sassDir
            srcFiles = ["${project.name}.sass"]
        } else if(scssDir.exists() && scssDir.isDirectory()) {
            srcDir = scssDir
            srcFiles = ["${project.name}.scss"]
        }
        cssPath = "META-INF/resources/webjars/${project.name}/${project.rootProject.version}"
    }

    static SassExtension get(Project project) {
        return project.extensions.getByType(SassExtension);
    }

    File getCssLocation() {
        return new File(destinationDir, cssPath)
    }

    File[] getCssFiles() {
        return srcFiles.collect { new File(cssLocation, it.replace('.sass', '.css').replace('.scss', '.css')) }
    }

    File[] getSassFiles() {
        return srcFiles.collect { new File(srcDir, it) }
    }

    PackageJson getPackageJson() {
        return PackageJson
                .initPrivate()
                .addDependencies(npmDependencies)
    }

    File getPackageJsonFile() {
        new File(nodeModulesDir, 'package.json')
    }

    List<String> getPlugins() {
        if (plugins == null) {
            return npmDependencies.findAll { it.key.startsWith('sass-plugin-') }.collect { it.key.substring(12) }
        }

        return plugins
    }

    Map<String, String> getPluginOptions() {
        return pluginOptions.clone() as Map<String, String>
    }
}
