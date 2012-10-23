package cc.catalysts.gradle.plugins.codegen.java

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.SourceSet
import cc.catalysts.gradle.plugins.codegen.CodegenTask

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenJavaPlugin implements Plugin<Project> {
	void apply(Project project) {
		
		if(project.tasks.findByPath('codegen') == null) {
			project.task('codegen', type: CodegenTask)
		}
		
		project.extensions.codegenjava = new CodegenJavaExtension()
		project.task('codegenJavaBuild', type: CodegenJavaTask)
		
		project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
			if(!sourceSet.name.toLowerCase().contains("test")) {
				def path = "${project.projectDir}/target/generated-sources/${sourceSet.name}/cp"
				project.extensions.codegenjava.destDirs.add path
				sourceSet.java { srcDir path }
			}
		}
	}
}