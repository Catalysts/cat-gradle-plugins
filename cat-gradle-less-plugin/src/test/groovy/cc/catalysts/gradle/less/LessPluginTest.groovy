package cc.catalysts.gradle.less

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
class LessPluginTest extends Specification  {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')



        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
    }

    def "less-install installs less + plugins"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.less'
            }
        """

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('less-install')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("less@2.5.3")
        result.output.contains("less-plugin-autoprefix@1.5.1")
        result.output.contains("less-plugin-clean-css@1.5.1")
        result.task(":less-install").outcome == SUCCESS
    }

    def "less-install correctly installs less + plugins after less-clean"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.less'
            }
        """

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('less-clean', 'less-install')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("less@2.5.3")
        result.output.contains("less-plugin-autoprefix@1.5.1")
        result.output.contains("less-plugin-clean-css@1.5.1")
        result.task(":less-install").outcome == SUCCESS
    }

    private List<File> pluginClasspath

}
