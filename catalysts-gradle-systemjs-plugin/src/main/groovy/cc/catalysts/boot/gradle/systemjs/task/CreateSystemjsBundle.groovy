package cc.catalysts.boot.gradle.systemjs.task

import cc.catalysts.boot.gradle.systemjs.SystemjsExtension
import com.moowork.gradle.node.task.NodeTask
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class CreateSystemjsBundle extends NodeTask {
    CreateSystemjsBundle() {
        setScript(new File(project.node.nodeModulesDir, 'node_modules/gulp/bin/gulp.js'))

        SystemjsExtension config = project.systemjs
        setArgs([
                "--project.version=${project.rootProject.version}",
                "--destination.dir=${config.destinationDir}",
                "--bundle.name=${project.name}-bundle.js",
                "--source.dir=${config.srcDir}",
                "--include.path=${config.includePath}"
        ])
    }

    //    @TaskAction
//    void createSystemjsBundle() {
//        SystemjsExtension config = project.catBootSystemjs;
//
//        File srcDir = config.srcDir
//        String include = "${srcDir}${File.separator}${config.includePath}"
//        File destinationDir = config.destinationDir
//
//
//
//        println "${include}->${destinationDir}"
//    }
}
