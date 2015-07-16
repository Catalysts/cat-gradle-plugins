package cc.catalysts.gradle.plugins.grails

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsTestCoverageTask extends DefaultTask {

    public GrailsTestCoverageTask() {
        this.dependsOn 'install-grails'
    }

    @TaskAction
    public void executeGrailsTestCoverage() {
        GrailsUtils.executeGrailsCommand(project, ["test-app", "-coverage", "-xml"], false)
    }

}
