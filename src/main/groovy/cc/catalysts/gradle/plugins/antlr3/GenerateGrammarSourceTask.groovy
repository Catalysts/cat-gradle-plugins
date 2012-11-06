package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultJavaExecAction
import org.gradle.process.internal.JavaExecAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GenerateGrammarSourceTask extends DefaultTask {
    @TaskAction
    def generateGrammarSource() {
        FileResolver fileResolver = getServices().get(FileResolver.class)
        JavaExecAction javaExec = new DefaultJavaExecAction(fileResolver);
        javaExec.setMain("org.antlr.Tool")
        javaExec.classpath(project.configurations.antlr3)

        println "cat-antlr3: generating from " + project.antlr3.grammarList.size() + " files"
        for (grammar in project.antlr3.grammarList) {
            def parts = grammar.split("->")
            if (parts.size() != 2){
                println "Could not run antlr for line '" + grammar + "'"
                throw new Exception("Invalid Input for cat-antlr3!")
            }
            def grammarFile = parts[0]
            def grammarPackage = parts[1]

            grammarFile = grammarFile.replace('.',File.separator)
            grammarFile += ".g"

            grammarPackage = grammarPackage.replace('.',File.separator)

            println "   Generating from '" + grammarFile + "' to '" + grammarPackage + "'"

            javaExec.args(["-o", "${project.antlr3.destinationDir}${File.separator}${grammarPackage}", grammarFile].flatten())

            javaExec.execute();
        }
    }
}