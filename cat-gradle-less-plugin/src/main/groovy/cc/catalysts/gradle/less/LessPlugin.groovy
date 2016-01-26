package cc.catalysts.gradle.less

import cc.catalysts.gradle.less.task.ExtractWebjars
import cc.catalysts.gradle.less.task.InstallLess
import cc.catalysts.gradle.less.task.Less2Css
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet

class LessPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyNodePluginAndDefaults(project)
        initConfiguration(project)
        registerTasks(project);
        addDestinationDirToSourceSets(project)
    }

    private void registerTasks(Project project) {
        Task installLess = project.task('installLess', type: InstallLess)
        Task extractWebjars = project.task('extract-webjars', type: ExtractWebjars)
        Task less = project.task('less',
                type: Less2Css,
                dependsOn: [installLess, extractWebjars],
                description: 'Prepares the less compiler and compiles your less to css')
        project.task('less2css', type: Less2Css)
        project.tasks.getByName('processResources').dependsOn(less)
    }

    private void addDestinationDirToSourceSets(Project project) {
        project.convention.plugins.java.sourceSets.forEach({ SourceSet sourceSet ->
            LessExtension config = LessExtension.get(project)
            sourceSet.resources { srcDir config.destinationDir };
        })
    }

    private void initConfiguration(Project project) {
        project.extensions.add('less', new LessExtension(project))
    }

    private void applyNodePluginAndDefaults(Project project) {
        project.plugins.apply('com.moowork.node')

        project.node.version = '5.5.0'
        project.node.download = true
    }
}