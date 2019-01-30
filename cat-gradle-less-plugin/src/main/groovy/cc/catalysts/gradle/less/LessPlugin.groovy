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
        if (!project.tasks.findByName('clean')) {
            project.task('cleanLess', type: CleanLess)
        }

        InstallLess installLess = project.tasks.create('less-install', InstallLess)
        ExtractWebjars extractWebjars = project.tasks.create('less-extract-webjars', ExtractWebjars)
        Less2Css less = project.tasks.create('less', Less2Css, {
                it.dependsOn = [installLess, extractWebjars]
                it.description = 'Prepares the less compiler and compiles your less to css'
        })

        // add the Less task type as property to the project to be able to use it within custom tasks
        project.extensions.extraProperties.Less = Less2Css

        Less2Css lessCompile = project.tasks.create('less-compile', Less2Css)
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
        project.extensions.create('less', LessExtension, project)
    }

    private void applyNodePluginAndDefaults(Project project) {
        project.plugins.apply('com.moowork.node')

        project.node.version = '8.15.0'
        project.node.download = true
    }
}