package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultJavaExecAction
import org.gradle.process.internal.JavaExecAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CompileGwtTask extends DefaultTask {

    CompileGwtTask() {
        dependsOn "cleanGwt"
    }

    @TaskAction
    def compile() {
        FileResolver fileResolver = getServices().get(FileResolver.class)

        for (module in project.gwt.modules) {
            logger.lifecycle '\n' + ('#' * 50) + '\n'
            logger.lifecycle "cat-gwt: Compiling ${module.name} with GWT ${project.gwtVersion} using the following jars:"


            project.ext.classpath_list = [
                    project.configurations.gwtCompile,
                    project.configurations.gwtBuild
            ]

            def printedPath = new HashSet()
            project.configurations.gwtCompile.each {
                File file -> if(!printedPath.contains(file.name)) {
                    logger.lifecycle '    ' + file.name
                    printedPath.add(file.name)
                }
            }
            project.configurations.gwtBuild.each {
                File file -> if(!printedPath.contains(file.name)) {
                    logger.lifecycle '    ' + file.name
                    printedPath.add(file.name)
                }
            }

            logger.lifecycle ''

            JavaExecAction javaExec = new DefaultJavaExecAction(fileResolver);

            javaExec.setMain('com.google.gwt.dev.Compiler')
            javaExec.classpath(project.ext.classpath_list)
            javaExec.args(module.modulename,
                    '-war', project.gwt.warFolder,
                    '-localWorkers', project.gwt.workers,
                    '-style', project.gwt.style)

            javaExec.execute();
        }
    }
}