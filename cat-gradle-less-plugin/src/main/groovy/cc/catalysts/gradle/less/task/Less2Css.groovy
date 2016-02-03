package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class Less2Css extends NodeTask {
    public Less2Css() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Compiles your less to css'

        project.afterEvaluate({
            LessExtension config = LessExtension.get(project)
            File lessc = new File(config.nodeModulesDir, 'node_modules/less/bin/lessc')
            setScript(lessc)

            getInputs().dir(config.srcDir)
            getOutputs().dir(config.nodeModulesDir)
            getOutputs().dir(config.destinationDir)
            setWorkingDir(NodeExtension.get(project).nodeModulesDir)
        })

    }

    @Override
    void exec() {
        LessExtension config = LessExtension.get(project)

        List<String> commonArgs = [
                "--include-path=${new File(project.getBuildDir(), 'cat-gradle/less/extracted/META-INF/resources')}"
        ]

        commonArgs.addAll(config.additionalArguments)

        def pluginOptions = config.pluginOptions
        for (String plugin : config.plugins) {
            String pluginArgs = pluginOptions.remove(plugin)
            commonArgs.add("--${plugin}=${pluginArgs ?: ''}")
        }

        if (!pluginOptions.isEmpty()) {
            logger.warn("Unused plugin options ${pluginOptions.keySet()} are configured! Please make sure you have no typos in your configuration.")
        }

        Map<String, String> globalVars = [:]

        project.configurations.forEach({ Configuration configuration ->
            configuration
                    .resolvedConfiguration
                    .resolvedArtifacts
                    .findAll({ it.moduleVersion.id.group.startsWith('org.webjars') })
                    .forEach({ ResolvedArtifact it ->
                String artifactId = "${it.name.replace('.', '-')}"
                globalVars.put("webjars-${artifactId}", "webjars/${it.name}/${it.moduleVersion.id.version}")
            })
        });

        for (Map.Entry<String, String> globalVar : globalVars) {
            commonArgs.add("--global-var=${globalVar.key}='${globalVar.value}'")
        }

        for (String srcFile : config.srcFiles) {
            List<String> argumentList = [
                    "${new File(config.srcDir, srcFile)}",
                    "${new File(config.cssLocation, srcFile.replace('.less', '.css'))}"
            ]
            argumentList.addAll(commonArgs)
            logger.lifecycle("Executing lessc: ${argumentList}")
            setArgs(argumentList)
            super.exec()
            getResult().assertNormalExitValue()
        }
    }
}
