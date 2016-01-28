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
}
""";


    public InstallLess() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Prepares the less compiler'


        project.afterEvaluate({
            File nodeModulesDir = LessExtension.get(project).nodeModulesDir

            if (!nodeModulesDir.exists() && !nodeModulesDir.mkdirs()) {
                throw new TaskConfigurationException(path, "Couldn't create ${nodeModulesDir}!", null)
            }

            File projectPackageJson = new File(nodeModulesDir, 'package.json')

            if (!projectPackageJson.exists()) {
                // first run
                outputs.upToDateWhen { false }
            } else if (!projectPackageJson.delete()) {
                throw new TaskConfigurationException(path, "Couldn't delete ${projectPackageJson}!", null)
            }

            projectPackageJson.text = PACKAGE_JSON

            if (!projectPackageJson.exists()) {
                throw new TaskConfigurationException(path, "Couldn't create ${projectPackageJson}!", null)
            }

            setWorkingDir(nodeModulesDir)
            setNpmCommand('install')

            inputs.file(projectPackageJson)
            outputs.dir(new File(nodeModulesDir, 'node_modules'))
        })
    }
}
