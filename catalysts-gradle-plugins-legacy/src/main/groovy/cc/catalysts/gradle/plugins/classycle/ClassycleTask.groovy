package cc.catalysts.gradle.plugins.classycle

import cc.catalysts.gradle.plugins.xslt.XsltTask
import cc.catalysts.gradle.utils.TCLogger
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

/**
 * TODO: add DEBUG messages
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class ClassycleTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    @InputFile
    public File getDefinitionFile() {
        return new File(project.projectDir.absolutePath, project.classycle.definitionFile as String)
    }

    @InputDirectory
    public File getClassesPath() {
        return new File(project.buildDir.absolutePath, "classes")
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return new File(project.projectDir.absolutePath, project.classycle.outputDirectory as String)
    }

    @OutputFile
    public File getGraphOutputFile() {
        return new File(getOutputDirectory(), project.classycle.graphOutputFile as String)
    }

    @OutputFile
    public File getCheckOutputFile() {
        return new File(getOutputDirectory(), project.classycle.checkOutputFile as String)
    }

    public ClassycleTask() {
        group = "Code Quality"
        description = "static java dependency checker"
    }

    @TaskAction
    void generate() {
        File f = getDefinitionFile();
        if (!f.exists()) {
            log.failure "classycle: could not find definition file '" + f.getAbsolutePath() + "'.", true
        }
        getOutputDirectory().mkdirs()

        // check dependency rules defined in the ddf file
        OutputStream out = new FileOutputStream(getCheckOutputFile())
        project.javaexec {
            classpath project.configurations.codequality
            main = "classycle.dependency.DependencyChecker"
            args = [
                    "-dependencies=@" + getDefinitionFile().getAbsolutePath(),
                    "-renderer=classycle.dependency.XMLResultRenderer",
                    getClassesPath().getAbsolutePath()
            ].toList()
            standardOutput out
        }
        out.flush();
        out.close();

        // generate xml report file of dependencies
        project.javaexec {
            classpath project.configurations.codequality
            main = "classycle.Analyser"
            args = [
                    "-xmlFile=" + getGraphOutputFile().getAbsolutePath(),
                    getClassesPath().getAbsolutePath()
            ].toList()
        }

        // copy resource files for rendering report to output directory
        URL inputUrl = getClass().getResource("/classycle/")
        File inputDirectory = new File(inputUrl.toURI());
        FileUtils.copyDirectory(inputDirectory, getOutputDirectory())

        // convert generated xml file to html
        project.task("classycleXslt", type: XsltTask, dependsOn: this) {
            group = "Code Quality"
            description = "convert xml output to html"
            source getOutputDirectory()
            include project.classycle.graphOutputFile
            destDir = getOutputDirectory()
            extension = 'html'
            stylesheetFile = new File(getOutputDirectory().getAbsolutePath(), 'reportXMLtoHTML.xsl')
        }.execute()
    }
}
