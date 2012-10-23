package cc.catalysts.gradle.plugins.jaxb.xsd

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import cc.catalysts.gradle.plugins.jaxb.GenerateJaxbTask

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class XsdPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.extensions.xsd = new XsdExtension()
		project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
			def schemasDir = "src/${sourceSet.name}/xsd"
			if(new File("${project.projectDir}/" + schemasDir).exists()) {
				setupXsdFor(sourceSet, project)
				sourceSet.java { srcDir generatedJavaDirFor(project, sourceSet) }
				sourceSet.resources { srcDir schemasDir }
			}
		}
	}
	
	private setupXsdFor(SourceSet sourceSet, Project project) {
		Task xsd = createXsdTaskFor(sourceSet, project)
		project.tasks[sourceSet.compileJavaTaskName].dependsOn(xsd)
		project.tasks["jaxb"].dependsOn(xsd)
	}
	
	private Task createXsdTaskFor(SourceSet sourceSet, Project project) {
		def xsdTask = project.tasks.add(taskName(sourceSet), GenerateJaxbTask)
		xsdTask.sourceDirectory = "src/${sourceSet.name}/xsd"
		xsdTask.group = GenerateJaxbTask.GENERATE_GROUP
		xsdTask.description = "Generates code from the ${sourceSet.name} XSD sources."
		xsdTask.outputDirectory = generatedJavaDirFor(project, sourceSet)
		xsdTask.conventionMapping.jaxbClasspath = {
			def jaxbClassPath = project.configurations.jaxb.copy()
			jaxbClassPath.transitive = true
			jaxbClassPath
		}
		xsdTask.convertList = project.extensions.xsd.convertList
		
		xsdTask
	}
	
	private File generatedJavaDirFor(Project project, SourceSet sourceSet) {
		project.file("${project.projectDir}/target/generated-sources/${sourceSet.name}/xsd")
	}

	private String taskName(SourceSet sourceSet) {
		return sourceSet.getTaskName('generate', 'XsdSource')
	}
}