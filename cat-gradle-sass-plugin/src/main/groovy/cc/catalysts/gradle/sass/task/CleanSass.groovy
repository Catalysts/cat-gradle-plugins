package cc.catalysts.gradle.sass.task

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction

class CleanSass extends DefaultTask {
    CleanSass() {
        description = 'Cleans output and working directory of sass/scss compiler'
        group = 'Cat-Boot SASS'
    }

    @TaskAction
    void clean() {
        Task sassTask = project.tasks.sass;
        project.delete(sassTask.outputs.files)
    }
}
