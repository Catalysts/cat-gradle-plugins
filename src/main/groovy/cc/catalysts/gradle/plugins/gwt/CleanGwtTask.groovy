package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CleanGwtTask extends DefaultTask {

    CleanGwtTask() {
        dependsOn "jar"
    }

    @TaskAction
    def run() {
        for (module in project.gwt.modules) {
            project.delete 'src/main/webapp/' + module.name
        }
        project.delete 'src/main/webapp/WEB-INF/deploy/'
    }
}