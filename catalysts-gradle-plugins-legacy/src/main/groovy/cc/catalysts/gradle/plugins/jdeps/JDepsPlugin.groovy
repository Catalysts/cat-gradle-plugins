package cc.catalysts.gradle.plugins.jdeps

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class JDepsPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.apply plugin: 'java'

		project.extensions.jdeps = new JDepsExtension()

		Task jdeps = project.task('jdeps', type: JDepsTask)
		project.tasks.getByName('check').dependsOn(jdeps)
	}
}
