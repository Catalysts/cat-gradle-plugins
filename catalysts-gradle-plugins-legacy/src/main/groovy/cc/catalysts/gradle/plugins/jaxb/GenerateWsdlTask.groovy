package cc.catalysts.gradle.plugins.jaxb

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class GenerateWsdlTask extends DefaultTask {
    public static final String GENERATE_GROUP = 'generate'
    private TCLogger log = new TCLogger(project, logger)

    GenerateWsdlTask() {
        convertList = []
        project.tasks.codegen.dependsOn(this)
    }

    @InputFiles
    FileCollection getJaxbClasspath() {
        null
    }

    File outputDirectory

    String sourceDirectory

    List<String> convertList

    @TaskAction
    def generate() {
        if (outputDirectory.exists()) {
            outputDirectory.deleteDir()
        }
        outputDirectory.mkdirs()
        for (jaxbFile in convertList) {
            def jaxbSrc = new DefaultSourceDirectorySet("JAXB source", project.fileResolver)
            jaxbSrc.filter.include(jaxbFile)
            jaxbSrc.srcDir sourceDirectory

            if (!(jaxbSrc.files.empty)) {
                log.lifecycle "Converting " + jaxbFile

                ant.taskdef(
                        name: 'genClassesFromWSDL',
                        classname: 'org.apache.axis.tools.ant.wsdl.Wsdl2javaAntTask',
                        classpath: jaxbClasspath.asPath
                )

                for (jaxbSource in jaxbSrc.files) {
                    ant.genClassesFromWSDL(
                            url: jaxbSource,
                            output: outputDirectory
                    )
                }

                if (JaxbPlugin.dirIsEmpty(outputDirectory)) {
                    log.failure("Error executing jaxb! Files not generated! Source: " + jaxbFile, true)
                }
            }
        }
    }

}