package cc.catalysts.gradle.plugins.codegen.java

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.text.SimpleDateFormat

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)
	
	CodegenJavaTask() {
		project.tasks.codegen.dependsOn(this)
		project.tasks.compileJava.dependsOn(this)
	}
	
	@TaskAction
	def build() {
		def pName = project.codegenjava.packageName as String
		if(pName != null) {
			Calendar calendar = Calendar.getInstance()
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm")
			def date = dateFormat.format(calendar.getTime())
			def version = project.version
            (project.codegenjava.destDirs as List<String>).each {
				File destDir = new File(it)
				if(destDir.exists()) {
					destDir.deleteDir()
				}
				new File(it + "/" + pName.replace('.', '/')).mkdirs()
				def f = new File(it + "/" + pName.replace('.', '/') + '/Build.java')
                log.lifecycle "Writing new file '${f.getAbsolutePath()}'"
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