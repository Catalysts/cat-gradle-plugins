package cc.catalysts.gradle.plugins.grails

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsTestTask extends DefaultTask {

    public GrailsTestTask() {
        this.dependsOn 'install-grails'
    }

    @TaskAction
    public void executeGrailsTest() {
        List<String> testCommand = ["test-app"];

        if (project.catgrails.coverage)
            testCommand.add("-coverage");

        GrailsUtils.executeGrailsCommand(project, testCommand, false);
    }

}
