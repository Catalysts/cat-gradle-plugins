package cc.catalysts.gradle.systemjs.task

import cc.catalysts.gradle.systemjs.SystemjsExtension
import cc.catalysts.gradle.systemjs.SystemjsPlugin
import com.moowork.gradle.node.task.NpmTask
import org.gradle.execution.commandline.TaskConfigurationException

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class InstallSystemjsBuilder extends NpmTask {
    public InstallSystemjsBuilder() {
        this.group = 'Cat-Boot'
        this.description = 'Prepares the systemjs builder'

        project.afterEvaluate({
            SystemjsExtension config = SystemjsExtension.get(project)

            outputs.upToDateWhen {
                return config
                        .getPackageJson()
                        .equals(config.getPackageJsonFile())
            }

            setWorkingDir(config.nodeModulesDir)
            setNpmCommand('install')

            outputs.dir(new File(config.nodeModulesDir, 'node_modules'))
        })
    }

    private void writePackageJson() {
        SystemjsExtension config = SystemjsExtension.get(project)
        File packageJsonFile = config.getPackageJsonFile()

        config.getPackageJson()
                .toFile(packageJsonFile)

        if (!packageJsonFile.exists()) {
            throw new TaskConfigurationException(path, "Couldn't create ${packageJsonFile}!", null)
        }

        logger.lifecycle("Successfully created ${packageJsonFile}")
    }

    private void writeSystemjsBundleScript() {
        File nodeModulesDir = SystemjsExtension.get(project).nodeModulesDir


        InputStream systemjsBundleScriptSource = SystemjsPlugin.classLoader.getResourceAsStream('cat-gradle/systemjs/systemjs-bundle.es6')

        File systemjsBundleScript = new File(nodeModulesDir, 'systemjs-bundle.es6')
        Files.copy(systemjsBundleScriptSource, systemjsBundleScript.toPath(), StandardCopyOption.REPLACE_EXISTING)

        if (!systemjsBundleScript.exists()) {
            throw new TaskConfigurationException(path, "Couldn't create ${systemjsBundleScript}!", null)
        }

        logger.lifecycle("Successfully created ${systemjsBundleScript}")
    }

    @Override
    void exec() {
        writePackageJson();
        writeSystemjsBundleScript();
        super.exec()
    }
}
