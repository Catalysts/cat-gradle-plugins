package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.task.NodeTask
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
            getOutputs().dir(config.cssFiles)
            setWorkingDir(NodeExtension.get(project).nodeModulesDir)
        })

    }

    @Override
    void exec() {
        LessExtension config = LessExtension.get(project)


        List<String> commonArgs = [
                '--source-map-less-inline',
                '--strict-units=on',
                '--autoprefix',
                '--clean-css',
                "--include-path=${new File(project.getBuildDir(), 'cat-gradle/less/extracted/META-INF/resources')}"
        ]

        project.configurations.forEach({ configuration ->
            configuration.dependencies.findAll { it.group.startsWith('org.webjars') } forEach {
                String artifactId = "${it.name.replace('.', '-')}"
                commonArgs.add("--global-var=webjars-${artifactId}='webjars/${it.name}/${it.version}'")
            }

        });

        for (String srcFile : config.srcFiles) {
            List<String> argumentList = [
                    "${new File(config.srcDir, srcFile)}",
                    "${new File(config.cssLocation, srcFile.replace('.less', '.css'))}"
            ]
            argumentList.addAll(commonArgs)
            setArgs(argumentList)
            super.exec()
            getResult().assertNormalExitValue()
        }
    }
}
