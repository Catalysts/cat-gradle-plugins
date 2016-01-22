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

        project.plugins.apply('com.moowork.node')
        project.extensions.add('systemjs', new SystemjsExtension(project))

        project.node.version = '5.4.1'
        project.node.npmVersion = '3.5.3'
        project.node.download = true

        project.convention.plugins.java.sourceSets.forEach({ SourceSet sourceSet ->
            SystemjsExtension config = project.systemjs;
            sourceSet.resources { srcDir config.destinationDir };
        })

        Task bundle = project.task('systemJsBundle',
                dependsOn: 'npmInstall',
                type: CreateSystemjsBundle,
                description: 'Creates a systemjs bunlde of all specified js files',
                group: 'cat-boot')
        project.tasks.getByName('processResources').dependsOn(bundle)

        Task clean = project.task('cleanSystemjsBundle',
                type: CleanSystemjsBundle,
                description: 'Cleans output directory of systemjs bundle',
                group: 'cat-boot')
        project.tasks.getByName('clean').dependsOn(clean)

    }
}




