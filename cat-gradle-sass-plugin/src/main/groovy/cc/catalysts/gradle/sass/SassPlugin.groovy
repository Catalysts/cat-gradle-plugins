package cc.catalysts.gradle.sass

import cc.catalysts.gradle.sass.task.CleanSass
import cc.catalysts.gradle.sass.task.ExtractWebjars
import cc.catalysts.gradle.sass.task.InstallSass
import cc.catalysts.gradle.sass.task.Sass2Css
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

class SassPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        applyNodePluginAndDefaults(project)
        initConfiguration(project)
        registerTasks(project);
        addDestinationDirToSourceSets(project)
    }

    private void registerTasks(Project project) {
        if (!project.tasks.findByName('clean')) {
            project.task('cleanSass', type: CleanSass)
        }

        InstallSass installSass = project.tasks.create('sass-install', InstallSass)
        ExtractWebjars extractWebjars = project.tasks.create('sass-extract-webjars', ExtractWebjars)
        Sass2Css sass = project.tasks.create('sass', Sass2Css, {
                it.dependsOn = [installSass, extractWebjars]
                it.description = 'Prepares the sass/scss compiler and compiles your sass and scss files to css'
        })

        // add the Sass task type as property to the project to be able to use it within custom tasks
        project.extensions.extraProperties.Sass = Sass2Css

        Sass2Css sassCompile = project.tasks.create('sass-compile', Sass2Css)
        dependsOnIfExists(project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME, sass)
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
            SassExtension config = SassExtension.get(project)
            project.logger.lifecycle("Injecting ${config.destinationDir} as output directory into main source set")
            SourceSet sourceSetMain = javaPlugin.sourceSets.main
            sourceSetMain.output.dir config.destinationDir, builtBy: 'sass';
        }
    }

    private void initConfiguration(Project project) {
        project.extensions.create('sass', SassExtension, project)
    }

    private void applyNodePluginAndDefaults(Project project) {
        if (!project.parent.hasProperty("skipApplyNodePlugin")) {
            project.plugins.apply('com.moowork.node')

            project.node.version = '8.15.0'
            project.node.download = true
        }
    }
}
