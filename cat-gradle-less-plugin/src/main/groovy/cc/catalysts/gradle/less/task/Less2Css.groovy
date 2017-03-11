package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.GradleHelper
import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.SkipWhenEmpty
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class Less2Css extends NodeTask {
    File srcDir
    String[] srcFiles
    String cssPath
    List<String> plugins;
    Map<String, String> pluginOptions
    List<String> additionalArguments
    Closure<String> cssFileName

    public Less2Css() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Compiles your less to css'

        project.afterEvaluate({
            LessExtension config = LessExtension.get(project)
            File lessc = new File(config.nodeModulesDir, 'node_modules/less/bin/lessc')
            setScript(lessc)

            getOutputs().dir(config.nodeModulesDir)
            getOutputs().dir(config.destinationDir)
            setWorkingDir(NodeExtension.get(project).nodeModulesDir)
        })
    }

    LessExtension getConfig() {
        return LessExtension.get(project)
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

    String getCssFileName(String lessFileName) {
        return cssFileName ? cssFileName(lessFileName) : config.cssFileName(lessFileName)
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
        List<String> commonArgs = [
                "--include-path=${new File(project.getBuildDir(), 'cat-gradle/less/extracted/META-INF/resources')}"
        ]

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
            logger.lifecycle("Executing lessc: ${argumentList}")
            setArgs(argumentList)
            super.exec()
            getResult().assertNormalExitValue()
        }
    }
}
