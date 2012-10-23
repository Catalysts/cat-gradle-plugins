package cc.catalysts.gradle.plugins.jaxb

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.internal.file.DefaultSourceDirectorySet

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class GenerateJaxbTask extends DefaultTask {
    public static final String GENERATE_GROUP = 'generate'
	
	GenerateJaxbTask() {
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
		if(outputDirectory.exists()) {
			outputDirectory.deleteDir()
		}
		outputDirectory.mkdirs()
		for (jaxbFile in convertList) {
			def jaxbSrc = new DefaultSourceDirectorySet("JAXB source", project.fileResolver)
			jaxbSrc.filter.include(jaxbFile)
			jaxbSrc.srcDir sourceDirectory
			
			if(!(jaxbSrc.files.empty)) {
				println "-- jaxb: converting " + jaxbFile
				
				ant.taskdef(name:'xjc',
					classname:'com.sun.tools.xjc.XJCTask',
					classpath:jaxbClasspath.asPath)
		 
				ant.xjc(extension: true, destdir: outputDirectory) {
					jaxbSrc.addToAntBuilder(ant, 'schema', FileCollection.AntType.FileSet)
					arg(value: "-verbose")
					arg(value: "-wsdl")
				}
				
				if(JaxbPlugin.dirIsEmpty(outputDirectory)) {
					throw new Exception("Error executing jaxb! Files not generated! Source: " + jaxbFile)
				}
			}
		}
	}
}