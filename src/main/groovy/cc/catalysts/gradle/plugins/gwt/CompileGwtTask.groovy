package cc.catalysts.gradle.plugins.gwt

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultJavaExecAction
import org.gradle.process.internal.JavaExecAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CompileGwtTask extends DefaultTask {
    private static final String GWT_COMPILE = "gwt-compile"
    private TCLogger log = new TCLogger(project, logger)

    CompileGwtTask() {
        dependsOn "cleanGwt"
    }

    @TaskAction
    def compile() {
        log.openBlock(GWT_COMPILE)
        FileResolver fileResolver = getServices().get(FileResolver.class)

        for (module in project.gwt.modules) {
            log.lifecycle '\n' + ('#' * 50) + '\n'
            log.lifecycle "cat-gwt: Compiling ${module.name} with GWT ${project.gwtVersion} using the following jars:"


            project.ext.classpath_list = [
                    project.configurations.gwtCompile,
                    project.configurations.gwtBuild
            ]

            def printedPath = new HashSet()
            project.configurations.gwtCompile.each {
                File file ->
                    if (!printedPath.contains(file.name)) {
                        log.lifecycle '    ' + file.name
                        printedPath.add(file.name)
                    }
            }
            project.configurations.gwtBuild.each {
                File file ->
                    if (!printedPath.contains(file.name)) {
                        log.lifecycle '    ' + file.name
                        printedPath.add(file.name)
                    }
            }

            log.lifecycle ''

            JavaExecAction javaExec = new DefaultJavaExecAction(fileResolver);

            javaExec.setMain('com.google.gwt.dev.Compiler')
            javaExec.classpath(project.ext.classpath_list)
            javaExec.args(module.modulename,
                    '-war', project.gwt.warFolder,
                    '-localWorkers', project.gwt.workers,
                    '-style', project.gwt.style)

            javaExec.execute();
        }
        log.closeBlock(GWT_COMPILE)
    }
}