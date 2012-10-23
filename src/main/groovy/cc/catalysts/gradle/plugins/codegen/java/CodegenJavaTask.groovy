package cc.catalysts.gradle.plugins.codegen.java

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.text.SimpleDateFormat

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaTask extends DefaultTask {
	
	CodegenJavaTask() {
		project.tasks.codegen.dependsOn(this)
		project.tasks.compileJava.dependsOn(this)
	}
	
	@TaskAction
	def build() {
		def pName = project.codegenjava.packageName
		if(pName != null) {
			Calendar calendar = Calendar.getInstance()
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm")
			def date = dateFormat.format(calendar.getTime())
			def version = project.version
			project.codegenjava.destDirs.each {
				File destDir = new File(it)
				if(destDir.exists()) {
					destDir.deleteDir()
				}
				new File(it + "/" + pName.replace('.', '/')).mkdirs()
				def f = new File(it + "/" + pName.replace('.', '/') + '/Build.java')
				def w = f.newWriter()
				
				w << 'package ' << pName << ';\r\n'
				w << '\r\npublic class Build {\r\n'
				w << '\tpublic static final String VERSION = "' + version + '";\r\n'
				w << '\tpublic static final String DATE = "' + date + '";\r\n'
				w << '}'
				
				w.close()
			}
		}
	}
}