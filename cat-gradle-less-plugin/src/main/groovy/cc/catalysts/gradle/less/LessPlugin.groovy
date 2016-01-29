package cc.catalysts.gradle.less

import cc.catalysts.gradle.less.task.CleanLess
import cc.catalysts.gradle.less.task.ExtractWebjars
import cc.catalysts.gradle.less.task.InstallLess
import cc.catalysts.gradle.less.task.Less2Css
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
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
        Task lessClean = project.task('less-clean', type: CleanLess)

        dependsOnIfExists(project, 'clean', lessClean)

        Task installLess = project.task('less-install', type: InstallLess)
        Task extractWebjars = project.task('less-extract-webjars', type: ExtractWebjars)
        Task less = project.task('less',
                type: Less2Css,
                dependsOn: [installLess, extractWebjars],
                description: 'Prepares the less compiler and compiles your less to css')
        project.task('less-compile', type: Less2Css)
        dependsOnIfExists(project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME, less)
    }

    private void dependsOnIfExists(Project project, String taskName, Task dependsOn) {
        Task task = project.tasks.findByName(taskName)
        if (task) {
            task.dependsOn(dependsOn)
        }
    }

    private void addDestinationDirToSourceSets(Project project) {
        JavaPluginConvention javaPlugin = project.convention.plugins.java

        if (javaPlugin) {
            LessExtension config = LessExtension.get(project)
            project.logger.lifecycle("Injecting ${config.destinationDir} as output directory into main source set")
            SourceSet sourceSetMain = javaPlugin.sourceSets.main
            sourceSetMain.output.dir config.destinationDir, builtBy: 'less';
        }
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