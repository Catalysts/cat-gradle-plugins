package cc.catalysts.gradle.less

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class LessPlugin implements Plugin<Project> {

    void apply(Project project) {

        def task = project.task('less',
                group: 'cat-less',
                description: 'Compiles your .less files into .css',
                type: LessTask)

        project.tasks.getByName('processResources').dependsOn(task)
    }

}