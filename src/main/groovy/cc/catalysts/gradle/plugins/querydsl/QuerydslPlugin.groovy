package cc.catalysts.gradle.plugins.querydsl

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class QuerydslPlugin implements Plugin<Project> {
	
	void apply(Project project) {
        project.apply plugin: 'java'

        project.extensions.querydsl = new QueryDslExtension()

        project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
            if(!sourceSet.name.toLowerCase().contains("test")) {
                sourceSet.java { srcDir project.querydsl.destinationDir }
            }
        }

        Task clean = project.task("cleanQueryDsl", type: CleanQueryDslTask, description: 'Cleans output directory of QueryDsl',group: "QueryDsl")
        project.tasks.getByName('clean').dependsOn(clean)
        Task create = project.task("createQueryDslOut", type: QueryDslOutputDirTask, dependsOn: clean, description: 'Creates output directory of QueryDsl',group: "QueryDsl")

        project.tasks.getByName('compileJava').dependsOn(create)

		project.compileJava {
			options.compilerArgs = [
					'-processor', 'com.mysema.query.apt.jpa.JPAAnnotationProcessor',
					'-s', project.projectDir.absolutePath + project.querydsl.destinationDir
            ]
		}
	}
}