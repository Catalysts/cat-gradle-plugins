package cc.catalysts.gradle.plugins.sass

import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class SassPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.task('sass',
                group: 'cat-sass',
                description: 'Compiles your .sass files into .css',
                type: cc.catalysts.gradle.plugins.sass.SassTask)
    }

}