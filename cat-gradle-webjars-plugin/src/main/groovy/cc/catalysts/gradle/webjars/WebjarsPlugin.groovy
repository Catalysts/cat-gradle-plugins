package cc.catalysts.gradle.webjars

import cc.catalysts.gradle.webjars.task.WebjarsClass
import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * This plugin is still incubating and may change completely an a future version!
 *
 * @author Thomas Scheinecker, Catalysts GmbH
 */
@Incubating
class WebjarsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply('java')

        project.extensions.create('webjars', WebjarsExtension, project)

        addDestinationDirToSourceSets(project)

        WebjarsClass webjarClass = project.tasks.create('webjarClass', WebjarsClass)

        project.tasks.getByName(JavaPlugin.COMPILE_JAVA_TASK_NAME).dependsOn(webjarClass);
    }


    private void addDestinationDirToSourceSets(Project project) {
        project.afterEvaluate({
            JavaPluginConvention javaPlugin = project.convention.plugins.java as JavaPluginConvention

            if (javaPlugin) {
                WebjarsExtension config = WebjarsExtension.get(project)
                SourceSet sourceSetMain = javaPlugin.sourceSets.main
                sourceSetMain.java.srcDir(config.destinationDir)
            }
        })
    }
}
