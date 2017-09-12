package cc.catalysts.gradle.sass.task

import cc.catalysts.gradle.GradleHelper
import cc.catalysts.gradle.sass.SassExtension
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.task.NodeTask
import groovy.json.JsonOutput
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.SkipWhenEmpty

class Sass2Css extends NodeTask {
    File srcDir
    String[] srcFiles
    String cssPath
    List<String> additionalArguments

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
    List<String> getAdditionalArguments() {
        return additionalArguments ?: config.additionalArguments
    }

    @Override
    void exec() {
        List<String> commonArgs = []

        commonArgs.addAll(getAdditionalArguments())

        Map<String, String> webjarMapping = [:]

        project.getConfigurations().findAll {
            GradleHelper.canBeResolved(it)
        }.each({ Configuration configuration ->
            configuration
                    .getResolvedConfiguration()
                    .getResolvedArtifacts()
                    .findAll({ it.moduleVersion.id.group.startsWith('org.webjars') })
                    .forEach({ ResolvedArtifact it ->
                String artifactId = "${it.name.replace('.', '-')}"

                File f = new File(SassExtension.get(project).nodeModulesDir, 'extracted')

                webjarMapping.put(artifactId, "${f.getAbsolutePath()}/META-INF/resources/webjars/${it.name}/${it.moduleVersion.id.version}")

            })
        });


        SassExtension config = SassExtension.get(project)

        File mappingFile = new File(config.getNodeModulesDir(), "mapping.json")
        mappingFile.newWriter().withWriter { w ->
            w << JsonOutput.toJson(webjarMapping)
        }

        File importerJS = new File(config.getNodeModulesDir(), "node-sass-webjar-importer.js")
        importerJS.newWriter().withWriter { w ->
            w << getClass().getClassLoader().getResource("node-sass-webjar-importer.js").getContent()
        }

        for (String srcFile : getSrcFiles()) {
            List<String> argumentList = [
                    "--importer", "${importerJS.getAbsolutePath()}",
                    "-o", "${cssLocation}",
                    "${new File(getSrcDir(), srcFile)}",
            ]
            argumentList.addAll(commonArgs)
            logger.lifecycle("Executing sassc: ${argumentList}")
            setArgs(argumentList)
            super.exec()
            getResult().assertNormalExitValue()

        }
    }
}
