package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.task.NpmTask
import org.gradle.execution.commandline.TaskConfigurationException

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class InstallLess extends NpmTask {
    public InstallLess() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Prepares the less compiler'

        project.afterEvaluate({
            LessExtension config = LessExtension.get(project)

            outputs.upToDateWhen {
                return config
                        .getPackageJson()
                        .equals(config.getPackageJsonFile())
            }

            setWorkingDir(config.nodeModulesDir)
            setNpmCommand('install')
            setArgs(["--verbose"])

            outputs.dir(new File(config.nodeModulesDir, 'node_modules'))
        })
    }

    private void writePackageJson() {
        LessExtension config = LessExtension.get(project)
        File packageJsonFile = config.getPackageJsonFile()

        config.getPackageJson()
                .toFile(packageJsonFile)

        if (!packageJsonFile.exists()) {
            throw new TaskConfigurationException(path, "Couldn't create ${packageJsonFile}!", null)
        }

        logger.lifecycle("Successfully created ${packageJsonFile}")
    }

    @Override
    void exec() {
        writePackageJson();
        super.exec()
    }
}
