package cc.catalysts.gradle.plugins.grails

import org.gradle.api.DefaultTask
import org.gradle.api.IllegalOperationAtExecutionTimeException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

public class WarTask extends DefaultTask {

	public WarTask() {
		dependsOn project.grails.application.tasks.'grails-war'
	}
	
	@TaskAction
	void rename() {
		def warFile = ~/.+\-[\w\d.]+\.war/
		def warDir = new File(project.grails.application.projectDir, 'target')
		def warDest = new File(warDir, 'ROOT.war')
		
		// delete old ROOT.war
		warDest.delete()
		
		
		def found = false
		
		warDir.eachFileMatch(warFile) {
			it.renameTo(warDest)
			found = true
		}
		
		if (!found) {
			throw new IllegalOperationAtExecutionTimeException ("Cant find generated war file in " + warDir)
		}
	}
}
