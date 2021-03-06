package cc.catalysts.gradle.less

import cc.catalysts.gradle.npm.AbstractNpmAwareExtension
import cc.catalysts.gradle.npm.PackageJson
import org.gradle.api.Project
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class LessExtension extends AbstractNpmAwareExtension {
    File srcDir
    String[] srcFiles
    String cssPath
    Map<String, String> npmDependencies = [
            'less'                  : '2.7.2',
            'less-plugin-autoprefix': '1.5.1',
            'less-plugin-clean-css' : '1.5.1'
    ]
    List<String> plugins;
    Map<String, String> pluginOptions = [:]
    List<String> additionalArguments = [
            '--strict-units=on'
    ]
    Closure<String> cssFileName = {
        return it.replace('.less', '.css')
    }

    LessExtension(Project project) {
        super(project, 'less')
        srcDir = new File(project.projectDir, 'src/main/resources/less')
        srcFiles = ["${project.name}.less"]
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
            return npmDependencies.findAll { it.key.startsWith('less-plugin-') }.collect { it.key.substring(12) }
        }

        return plugins
    }

    Map<String, String> getPluginOptions() {
        return pluginOptions.clone() as Map<String, String>
    }
}
