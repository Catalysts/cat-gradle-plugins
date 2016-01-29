package cc.catalysts.gradle.systemjs

import cc.catalysts.gradle.systemjs.task.CleanSystemjsBundle
import cc.catalysts.gradle.systemjs.task.CreateSystemjsBundle
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

class SystemjsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        applyNodePluginAndDefaults(project)
        project.extensions.add('systemjs', new SystemjsExtension(project))

        addDestinationDirToSourceSets(project)

        Task bundle = project.task('systemJsBundle',
                dependsOn: 'npmInstall',
                type: CreateSystemjsBundle,
                description: 'Creates a systemjs bundle of all specified js files',
                group: 'cat-boot')
        project.tasks.getByName(JavaPlugin.PROCESS_RESOURCES_TASK_NAME).dependsOn(bundle)

        if (!project.tasks.findByName('clean')) {
            project.task('cleanSystemJsBundle', type: CleanSystemjsBundle)
        }
    }

    private void addDestinationDirToSourceSets(Project project) {
        JavaPluginConvention javaPlugin = project.convention.plugins.java as JavaPluginConvention

        if (javaPlugin) {
            SystemjsExtension config = SystemjsExtension.get(project)
            project.logger.lifecycle("Injecting ${config.destinationDir} as output directory into main source set")
            SourceSet sourceSetMain = javaPlugin.sourceSets.main
            sourceSetMain.output.dir config.destinationDir, builtBy: 'systemJsBundle';
        }
    }

    private void applyNodePluginAndDefaults(Project project) {
        project.plugins.apply('com.moowork.node')

        project.node.version = '5.5.0'
        project.node.download = true
    }
}




