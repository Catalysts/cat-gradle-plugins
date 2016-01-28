package cc.catalysts.gradle.buildinfo

import cc.catalysts.gradle.buildinfo.task.BuildInfoTask
import cc.catalysts.gradle.buildinfo.task.CleanBuildInfo
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet

/**
 * @author Klaus Lehner
 */
class BuildInfoPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.add('buildinfo', new BuildInfoExtension(project))

        BuildInfoExtension config = BuildInfoExtension.get(project);
        SourceSet sourceSetMain = project.convention.plugins.java.sourceSets.main
        sourceSetMain.java.srcDir config.destinationDir

        Task buildInfo = project.task('buildinfo',
                type: BuildInfoTask,
                description: 'Generates a class that holds information about the build such as the build number',
                group: 'cat-boot')

        project.tasks.getByName('compileJava').dependsOn(buildInfo);

        Task clean = project.task('cleanBuildInfo',
                type: CleanBuildInfo,
                description: 'Cleans the build info',
                group: 'cat-boot')
        project.tasks.getByName('clean').dependsOn(clean)
    }
}
