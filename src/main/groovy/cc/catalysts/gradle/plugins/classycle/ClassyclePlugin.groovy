package cc.catalysts.gradle.plugins.classycle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class ClassyclePlugin implements Plugin<Project> {
	void apply(Project project) {
		project.apply plugin: 'java'

		project.extensions.classycle = new ClassycleExtension()

		project.configurations {
			codequality
		}

		project.repositories {
			mavenCentral()
		}

		if (!project.hasProperty("classycleVersion")) {
			project.ext.classycleVersion = '1.4.3'
		}

		project.dependencies {
			codequality (
				[group: 'org.specs2', name: 'classycle', version: project.classycleVersion]
			)
		}

		Task classycle = project.task("classycle", type: ClassycleTask)
		project.tasks.getByName('check').dependsOn(classycle)
	}
}
