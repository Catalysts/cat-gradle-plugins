package cc.catalysts.boot.gradle.systemjs

import cc.catalysts.boot.gradle.systemjs.task.CleanSystemjsBundle
import cc.catalysts.boot.gradle.systemjs.task.CreateSystemjsBundle
import cc.catalysts.boot.gradle.systemjs.task.CreateSystemjsWebjarConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class SystemjsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        project.plugins.apply('com.moowork.node')
        project.extensions.add('systemjs', new SystemjsExtension(project))

        project.node.version = '5.4.1'
        project.node.npmVersion = '3.5.3'
        project.node.download = true

        Task prepareNode = project.task('prepareNode',
                dependsOn: 'npmInstall',
                type: CreateSystemjsBundle,
                description: 'Creates a systemjs bunlde of all specified js files',
                group: 'cat-boot')

        Task webjarConfig = project.task('webjarConfig',
                type: CreateSystemjsWebjarConfig,
                description: 'Creates a systemjs configuration for all webjars dependencies',
                group: 'cat-boot')

        Task bundle = project.task('systemjsBundle',
                dependsOn: 'npmInstall',
                type: CreateSystemjsBundle,
                description: 'Creates a systemjs bunlde of all specified js files',
                group: 'cat-boot')
        project.tasks.getByName('assemble').dependsOn(bundle)

        Task clean = project.task('cleanSystemjsBundle',
                type: CleanSystemjsBundle,
                description: 'Cleans output directory of systemjs bundle',
                group: 'cat-boot')
        project.tasks.getByName('clean').dependsOn(clean)

    }
}




