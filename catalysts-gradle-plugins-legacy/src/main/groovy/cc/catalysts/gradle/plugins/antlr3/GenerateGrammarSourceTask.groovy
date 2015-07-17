package cc.catalysts.gradle.plugins.antlr3

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultJavaExecAction
import org.gradle.process.internal.JavaExecAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GenerateGrammarSourceTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @InputDirectory
    def inputDir = new File(project.projectDir, project.antlr3.antlrSource as String)
    @OutputDirectory
    def outputDir = new File(project.projectDir, project.antlr3.destinationDir as String)

    @TaskAction
    def generateGrammarSource() {
        log.info("input dir: $inputDir")
        log.info("output dir: $outputDir")

        FileResolver fileResolver = getServices().get(FileResolver.class)

        Map<String, FileTree> grammarMap = new HashMap<String, FileTree>()
        log.lifecycle "Generating from " + project.antlr3.grammarList.size() + " files"
        for (String grammar in project.antlr3.grammarList) {
            def parts = grammar.split("->")
            if (parts.size() != 2) {
                log.failure "Could not run antlr for line '" + grammar + "'", true
            }
            String grammarFile = parts[0]
            String grammarPackage = parts[1]

            if (!grammarMap.containsKey(grammarPackage)) {
                grammarMap.put(grammarPackage, project.fileTree(dir: project.antlr3.antlrSource))
            }

            grammarMap.get(grammarPackage).include grammarFile
        }

        for (m in grammarMap) {
            String grammarPackage = m.getKey()
            FileTree tree = m.getValue()

            log.lifecycle "Generating from " + tree.files.size() + " grammar files  to '" + grammarPackage + "'"

            grammarPackage = grammarPackage.replace('.', File.separator)

            JavaExecAction javaExec = new DefaultJavaExecAction(fileResolver)
            javaExec.setMain("org.antlr.Tool")
            javaExec.classpath(project.configurations.antlr3)
            javaExec.args(["-o", "${project.antlr3.destinationDir}${File.separator}${grammarPackage}", tree.files].flatten())
            javaExec.execute();
        }
    }
}