package cc.catalysts.gradle.sass.task

import cc.catalysts.gradle.sass.SassExtension
import com.moowork.gradle.node.task.NpmTask
import org.gradle.execution.commandline.TaskConfigurationException

class InstallSass extends NpmTask {
    public InstallSass() {
        this.group = 'Cat-Boot SASS'
        this.description = 'Prepares the sass/scss compiler'

        project.afterEvaluate({
            SassExtension config = SassExtension.get(project)

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
        SassExtension config = SassExtension.get(project)
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
