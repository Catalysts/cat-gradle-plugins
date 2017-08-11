package cc.catalysts.gradle.sass.task

import cc.catalysts.gradle.GradleHelper
import cc.catalysts.gradle.sass.SassExtension
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty

class Sass2Css extends NodeTask {
    File srcDir
    String[] srcFiles
    String cssPath
    List<String> plugins;
    Map<String, String> pluginOptions
    List<String> additionalArguments
    Closure<String> cssFileName

    public Sass2Css() {
        this.group = 'Cat-Boot SASS'
        this.description = 'Compiles your sass/scss to css'

        project.afterEvaluate({
            SassExtension config = SassExtension.get(project)
            File sassc = new File(config.nodeModulesDir, 'node_modules/node-sass/bin/node-sass')
            setScript(sassc)

            getOutputs().dir(config.nodeModulesDir)
            getOutputs().dir(config.destinationDir)
            setWorkingDir(NodeExtension.get(project).nodeModulesDir)
        })
    }

    SassExtension getConfig() {
        return SassExtension.get(project)
    }

    @InputDirectory
    File getSrcDir() {
        return srcDir ?: config.srcDir
    }

    @SkipWhenEmpty
    String[] getSrcFiles() {
        return srcFiles ?: config.srcFiles
    }

    File getCssLocation() {
        return cssPath ? new File(config.destinationDir, cssPath) : config.cssLocation;
    }

    @Input
    List<String> getPlugins() {
        return plugins ?: config.plugins
    }

    @Input
    Map<String, String> getPluginOptions() {
        return pluginOptions ?: config.pluginOptions
    }

    @Input
    List<String> getAdditionalArguments() {
        return additionalArguments ?: config.additionalArguments
    }

    String getCssFileName(String sassFileName) {
        return cssFileName ? cssFileName(sassFileName) : config.cssFileName(sassFileName)
    }

    @OutputFiles
    List<File> getCssFiles() {
        return getSrcFiles()
                .collect({
            return new File(getCssLocation(), getCssFileName(it))
        })
    }

    @Override
    void exec() {
        List<String> commonArgs = [ ]

        commonArgs.addAll(getAdditionalArguments())

        def pluginOptions = getPluginOptions()
        for (String plugin : getPlugins()) {
            String pluginArgs = pluginOptions.remove(plugin)
            commonArgs.add("--${plugin}=${pluginArgs ?: ''}")
        }

        if (!pluginOptions.isEmpty()) {
            logger.warn("Unused plugin options ${pluginOptions.keySet()} are configured! Please make sure you have no typos in your configuration.")
        }

        Map<String, String> globalVars = [:]

        project.getConfigurations().findAll {
            GradleHelper.canBeResolved(it)
        }.each({ Configuration configuration ->
            configuration
                    .getResolvedConfiguration()
                    .getResolvedArtifacts()
                    .findAll({ it.moduleVersion.id.group.startsWith('org.webjars') })
                    .forEach({ ResolvedArtifact it ->
                String artifactId = "${it.name.replace('.', '-')}"
                globalVars.put("webjars-${artifactId}", "webjars/${it.name}/${it.moduleVersion.id.version}")
            })
        });

        for (Map.Entry<String, String> globalVar : globalVars) {
            commonArgs.add("--global-var=${globalVar.key}='${globalVar.value}'")
        }

        for (String srcFile : getSrcFiles()) {
            List<String> argumentList = [
                    "${new File(getSrcDir(), srcFile)}",
                    "${new File(cssLocation, getCssFileName(srcFile))}"
            ]
            argumentList.addAll(commonArgs)
            logger.lifecycle("Executing sassc: ${argumentList}")
            setArgs(argumentList)
            super.exec()
            getResult().assertNormalExitValue()
        }
    }
}
