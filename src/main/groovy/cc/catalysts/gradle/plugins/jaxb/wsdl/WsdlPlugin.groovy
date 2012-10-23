package cc.catalysts.gradle.plugins.jaxb.wsdl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import cc.catalysts.gradle.plugins.jaxb.GenerateWsdlTask

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class WsdlPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.extensions.wsdl = new WsdlExtension()
		project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
			def schemasDir = "src/${sourceSet.name}/wsdl"
			if(new File("${project.projectDir}/" + schemasDir).exists()) {
				setupWsdlFor(sourceSet, project)
				sourceSet.java { srcDir generatedJavaDirFor(project, sourceSet) }
				sourceSet.resources { srcDir schemasDir }
			}
		}
	}
	
	private setupWsdlFor(SourceSet sourceSet, Project project) {
		Task wsdl = createWsdlTaskFor(sourceSet, project)
		project.tasks[sourceSet.compileJavaTaskName].dependsOn(wsdl)
		project.tasks["jaxb"].dependsOn(wsdl)
	}
	
	private Task createWsdlTaskFor(SourceSet sourceSet, Project project) {
		def wsdlTask = project.tasks.add(taskName(sourceSet), GenerateWsdlTask)
		wsdlTask.sourceDirectory = "src/${sourceSet.name}/wsdl"
		wsdlTask.group = GenerateWsdlTask.GENERATE_GROUP
		wsdlTask.description = "Generates code from the ${sourceSet.name} WSDL sources."
		wsdlTask.outputDirectory = generatedJavaDirFor(project, sourceSet)
		wsdlTask.conventionMapping.jaxbClasspath = {
			def jaxbClassPath = project.configurations.jaxb.copy()
			jaxbClassPath.transitive = true
			jaxbClassPath + project.configurations.compile.copy()
		}
		wsdlTask.convertList = project.extensions.wsdl.convertList
		
		wsdlTask
	}
	
	private File generatedJavaDirFor(Project project, SourceSet sourceSet) {
		project.file("${project.projectDir}/target/generated-sources/${sourceSet.name}/wsdl")
	}

	private String taskName(SourceSet sourceSet) {
		return sourceSet.getTaskName('generate', 'WsdlSource')
	}
}