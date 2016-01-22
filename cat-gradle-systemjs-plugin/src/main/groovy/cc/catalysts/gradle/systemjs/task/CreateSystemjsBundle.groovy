package cc.catalysts.gradle.systemjs.task

import cc.catalysts.gradle.systemjs.SystemjsExtension
import com.moowork.gradle.node.task.NodeTask

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class CreateSystemjsBundle extends NodeTask {
    CreateSystemjsBundle() {
        project.afterEvaluate({
            setScript(new File(project.node.nodeModulesDir, 'node_modules/gulp/bin/gulp.js'))

            SystemjsExtension config = project.systemjs
            setArgs([
                    "--project.version=${project.rootProject.version}",
                    "--destination.dir=${config.getBundleLocation()}",
                    "--bundle.name=${project.name}-bundle.js",
                    "--source.dir=${config.srcDir}",
                    "--include.path=${config.includePath}"
            ])
        })
    }
}
