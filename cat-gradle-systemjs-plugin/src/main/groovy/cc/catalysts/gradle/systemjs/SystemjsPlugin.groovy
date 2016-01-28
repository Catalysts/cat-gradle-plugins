package cc.catalysts.gradle.systemjs

import cc.catalysts.gradle.systemjs.task.CleanSystemjsBundle
import cc.catalysts.gradle.systemjs.task.CreateSystemjsBundle
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet

class SystemjsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        applyNodePluginAndDefaults(project)
        project.extensions.add('systemjs', new SystemjsExtension(project))

        SystemjsExtension config = SystemjsExtension.get(project)
        SourceSet sourceSetMain = project.convention.plugins.java.sourceSets.main
        sourceSetMain.output.dir config.destinationDir, builtBy: 'systemJsBundle';

        Task bundle = project.task('systemJsBundle',
                dependsOn: 'npmInstall',
                type: CreateSystemjsBundle,
                description: 'Creates a systemjs bundle of all specified js files',
                group: 'cat-boot')
        project.tasks.getByName('processResources').dependsOn(bundle)

        Task clean = project.task('cleanSystemjsBundle',
                type: CleanSystemjsBundle,
                description: 'Cleans output directory of systemjs bundle',
                group: 'cat-boot')
        project.tasks.getByName('clean').dependsOn(clean)
    }

    private void applyNodePluginAndDefaults(Project project) {
        project.plugins.apply('com.moowork.node')

        project.node.version = '5.5.0'
        project.node.download = true
    }
}




