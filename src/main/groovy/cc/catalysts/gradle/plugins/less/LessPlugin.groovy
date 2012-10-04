package cc.catalysts.gradle.plugins.less

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class LessPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.task('less',
                group: 'cat-less',
                description: 'Compiles your .less files into .css',
                type: LessTask)
    }

}