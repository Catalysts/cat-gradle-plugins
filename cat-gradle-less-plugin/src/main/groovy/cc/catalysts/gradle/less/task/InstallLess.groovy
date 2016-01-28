package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.task.NpmTask
import org.gradle.execution.commandline.TaskConfigurationException

import java.nio.file.Files

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class InstallLess extends NpmTask {
    public InstallLess() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Prepares the less compiler'


        project.afterEvaluate({
            File nodeModulesDir = LessExtension.get(project).nodeModulesDir

            if (!nodeModulesDir.exists() && !nodeModulesDir.mkdirs()) {
                throw new TaskConfigurationException(path, "Couldn't create ${nodeModulesDir}", null)
            }

            File projectPackageJson = new File(nodeModulesDir, 'package.json')

            InputStream packageJson = InstallLess.classLoader.getResourceAsStream('cat-gradle/less/package.json')
            List<String> lines = packageJson.readLines('utf-8')
            Files.write(projectPackageJson.toPath(), lines).toFile().exists()

            setWorkingDir(nodeModulesDir)
            setNpmCommand('install')

            inputs.file(projectPackageJson)
            outputs.dir(new File(nodeModulesDir, 'node_modules'))
        })
    }

    @Override
    void exec() {
        println "node.download ${NodeExtension.get( this.project ).download}"
        println "node.nodeModulesDir ${NodeExtension.get( this.project ).nodeModulesDir}"
        println "runner.workdir ${runner.workingDir}"
        super.exec()
    }
}
