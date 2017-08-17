package cc.catalysts.gradle.sass

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class SassPluginTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    Project project = ProjectBuilder.builder().build()

    File buildFile


    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
    }

    def "sass-install installs sass + plugins"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.sass'
            }
        """

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('sass-install')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("node-sass@4.5.3")
        result.task(":sass-install").outcome == SUCCESS
    }

    def "sass-install correctly installs sass + plugins after sass-clean"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.sass'
            }
        """
        testProjectDir.newFolder('build', 'cat-gradle', 'sass')
        testProjectDir.newFile('build/cat-gradle/sass/package.json') << '{"private":true}'

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('cleanSass', 'sass-install')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("node-sass@4.5.3")
        result.task(":cleanSass").outcome == SUCCESS
        result.task(":sass-install").outcome == SUCCESS
    }

    def "sass compiles sass file to css"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.sass'
            }
        """
        testProjectDir.newFolder('build', 'cat-gradle', 'sass')
        testProjectDir.newFile('build/cat-gradle/sass/package.json') << '{"private":true}'

        testProjectDir.newFolder('src', 'main', 'resources', 'sass')
        testProjectDir.newFile("src/main/resources/sass/${testProjectDir.getRoot().getName()}.sass") << '''
body
    color: green
        '''

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('cleanSass', 'sass')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("node-sass@4.5.3")
        result.task(":cleanSass").outcome == SUCCESS
        File css = new File(testProjectDir.getRoot(),
                "/build/generated-resources/cat-sass/META-INF/resources/webjars/${testProjectDir.getRoot().getName()}/unspecified/${testProjectDir.getRoot().getName()}.css"
        )
        css.exists()
        css.isFile()
        css.canRead()

        Scanner scanner = new Scanner(css)
        scanner.nextLine() == "body {"
        scanner.nextLine().trim() == "color: green; }"
        !scanner.hasNextLine()

    }

    def "sass compiler includes sass files"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.sass'
            }
        """
        testProjectDir.newFolder('build', 'cat-gradle', 'sass')
        testProjectDir.newFile('build/cat-gradle/sass/package.json') << '{"private":true}'

        testProjectDir.newFolder('src', 'main', 'resources', 'sass')
        testProjectDir.newFile("src/main/resources/sass/${testProjectDir.getRoot().getName()}.sass") << '''
@import "colors"
body 
    color: $color
        '''

        testProjectDir.newFile("src/main/resources/sass/colors.sass") << '''
$color: green
        '''

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('cleanSass', 'sass')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("node-sass@4.5.3")
        result.task(":cleanSass").outcome == SUCCESS
        File css = new File(testProjectDir.getRoot(),
                "/build/generated-resources/cat-sass/META-INF/resources/webjars/${testProjectDir.getRoot().getName()}/unspecified/${testProjectDir.getRoot().getName()}.css"
        )
        css.exists()
        css.isFile()
        css.canRead()

        Scanner scanner = new Scanner(css)
        scanner.nextLine() == "body {"
        scanner.nextLine().trim() == "color: green; }"
        !scanner.hasNextLine()
    }

    def "scss compiles scss file to css"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.sass'
            }
        """
        testProjectDir.newFolder('build', 'cat-gradle', 'sass')
        testProjectDir.newFile('build/cat-gradle/sass/package.json') << '{"private":true}'

        testProjectDir.newFolder('src', 'main', 'resources', 'scss')
        testProjectDir.newFile("src/main/resources/scss/${testProjectDir.getRoot().getName()}.scss") << '''
            body {
                color: green;
            }
        '''

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('cleanSass', 'sass')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("node-sass@4.5.3")
        result.task(":cleanSass").outcome == SUCCESS
        File css = new File(testProjectDir.getRoot(),
                "/build/generated-resources/cat-sass/META-INF/resources/webjars/${testProjectDir.getRoot().getName()}/unspecified/${testProjectDir.getRoot().getName()}.css"
        )
        css.exists()
        css.isFile()
        css.canRead()

        Scanner scanner = new Scanner(css)
        scanner.nextLine() == "body {"
        scanner.nextLine().trim() == "color: green; }"
        !scanner.hasNextLine()

    }

    def "scss compiler includes scss files"() {
        given:
        buildFile << """
            plugins {
                id 'cc.catalysts.sass'
            }
        """
        testProjectDir.newFolder('build', 'cat-gradle', 'sass')
        testProjectDir.newFile('build/cat-gradle/sass/package.json') << '{"private":true}'

        testProjectDir.newFolder('src', 'main', 'resources', 'scss')
        testProjectDir.newFile("src/main/resources/scss/${testProjectDir.getRoot().getName()}.scss") << '''
            @import "colors";
            body {
                color: $color;
            }
        '''

        testProjectDir.newFile("src/main/resources/scss/colors.scss") << '''
            $color: green;
        '''

        when:
        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('cleanSass', 'sass')
                .withPluginClasspath(pluginClasspath)
                .build()

        then:
        result.output.contains("node-sass@4.5.3")
        result.task(":cleanSass").outcome == SUCCESS
        File css = new File(testProjectDir.getRoot(),
                "/build/generated-resources/cat-sass/META-INF/resources/webjars/${testProjectDir.getRoot().getName()}/unspecified/${testProjectDir.getRoot().getName()}.css"
        )
        css.exists()
        css.isFile()
        css.canRead()

        Scanner scanner = new Scanner(css)
        scanner.nextLine() == "body {"
        scanner.nextLine().trim() == "color: green; }"
        !scanner.hasNextLine()
    }

    private List<File> pluginClasspath

}
