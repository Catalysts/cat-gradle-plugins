package cc.catalysts.gradle.less.task

import cc.catalysts.gradle.less.LessExtension
import com.moowork.gradle.node.task.NpmTask
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class InstallLess extends NpmTask {
    public InstallLess() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Prepares the less compiler'
        File nodeModulesDir = LessExtension.get(project).nodeModulesDir
        setWorkingDir(nodeModulesDir)
        setNpmCommand('install', 'less', 'less-plugin-autoprefix',  'less-plugin-clean-css')
    }
}
