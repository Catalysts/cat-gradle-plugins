package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.file.FileTree
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

        inputs.dir project.antlr3.antlrSource
        outputs.dir project.antlr3.destinationDir

        Map<String, FileTree> grammarMap = new HashMap<String, FileTree>()
        println "generating from " + project.antlr3.grammarList.size() + " files"
        for (grammar in project.antlr3.grammarList) {
            def parts = grammar.split("->")
            if (parts.size() != 2){
                println "Could not run antlr for line '" + grammar + "'"
                throw new Exception("Invalid Input for cat-antlr3!")
            }
            String grammarFile = parts[0]
            String grammarPackage = parts[1]

            if (!grammarMap.containsKey(grammarPackage)){
                grammarMap.put(grammarPackage, project.fileTree(dir: project.antlr3.antlrSource))
            }

            grammarMap.get(grammarPackage).include grammarFile
        }

        for(m in grammarMap){
            String grammarPackage =  m.getKey()
            FileTree tree = m.getValue()

            println "   Generating from " + tree.files.size() + " grammar files  to '" + grammarPackage + "'"

            grammarPackage = grammarPackage.replace('.',File.separator)

            JavaExecAction javaExec = new DefaultJavaExecAction(fileResolver)
            javaExec.setMain("org.antlr.Tool")
            javaExec.classpath(project.configurations.antlr3)
            javaExec.args(["-o", "${project.antlr3.destinationDir}${File.separator}${grammarPackage}", tree.files].flatten())
            javaExec.execute();
        }
    }
}