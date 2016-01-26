package cc.catalysts.gradle.less.task

import com.moowork.gradle.node.task.NpmTask
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class InstallLess extends NpmTask {
    public InstallLess() {
        this.group = 'Cat-Boot LESS'
        this.description = 'Prepares the less compiler'
        setNpmCommand('install', 'less', 'less-plugin-autoprefix',  'less-plugin-clean-css')
    }
}
