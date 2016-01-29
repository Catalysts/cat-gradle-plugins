package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.task.NpmTask
import org.gradle.execution.commandline.TaskConfigurationException

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class InstallLess extends NpmTask {
    private static final PACKAGE_JSON = """{
  "private": true,
  "dependencies": {
    "less": "2.5.3",
    "less-plugin-autoprefix": "1.5.1",
    "less-plugin-clean-css": "1.5.1"
  }
}""";

    public InstallLess() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Prepares the less compiler'


        project.afterEvaluate({
            File nodeModulesDir = LessExtension.get(project).nodeModulesDir

            File projectPackageJson = new File(nodeModulesDir, 'package.json')

            outputs.upToDateWhen {
                if (!projectPackageJson.exists()) {
                    // first run
                    return false
                }

                String current = projectPackageJson.readLines().join('\n')
                boolean equals = current.replaceAll(/\r/, '').equals(InstallLess.PACKAGE_JSON)
                if (equals) {
                    logger.debug('less-install package.json wasn\'t modified')
                } else {
                    logger.debug('less-install package.json was modified')
                }
                return equals
            }
            if (!projectPackageJson.exists()) {
                // first run
            } else {
                outputs.upToDateWhen {
                    def current = projectPackageJson.readLines().join('\n')
                    boolean equals = current.replaceAll(/\r/, '').equals(InstallLess.PACKAGE_JSON)
                    if (equals) {
                        logger.debug('less-install package.json wasn\'t modified')
                    } else {
                        logger.debug('less-install package.json was modified')
                    }
                    return equals
                }
            }

            setWorkingDir(nodeModulesDir)
            setNpmCommand('install')

            outputs.dir(new File(nodeModulesDir, 'node_modules'))
        })
    }

    private void writePackageJson() {
        File nodeModulesDir = LessExtension.get(project).nodeModulesDir

        if (!nodeModulesDir.exists() && !nodeModulesDir.mkdirs()) {
            throw new TaskConfigurationException(path, "Couldn't create ${nodeModulesDir}!", null)
        }

        File projectPackageJson = new File(nodeModulesDir, 'package.json')
        if (!projectPackageJson.exists()) {
            // first run
            logger.lifecycle("No previous ${projectPackageJson} file available - writing new one")
        } else if (!projectPackageJson.delete()) {
            throw new TaskConfigurationException(path, "Couldn't delete ${projectPackageJson}!", null)
        } else {
            logger.lifecycle("Successfully deleted ${projectPackageJson}")
        }

        projectPackageJson.text = PACKAGE_JSON

        if (!projectPackageJson.exists()) {
            throw new TaskConfigurationException(path, "Couldn't create ${projectPackageJson}!", null)
        }

        logger.lifecycle("Successfully created ${projectPackageJson}")
    }

    @Override
    void exec() {
        writePackageJson();
        super.exec()
    }
}
