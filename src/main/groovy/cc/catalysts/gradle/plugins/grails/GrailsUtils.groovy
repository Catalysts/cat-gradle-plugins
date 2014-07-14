package cc.catalysts.gradle.plugins.grails

import cc.catalysts.gradle.utils.TCLogger
import com.connorgarvey.gradlegrails.Path
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.gradle.logging.ShowStacktrace
import org.gradle.process.ExecResult

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsUtils {

    static boolean isGrailsApplication(Project project) {
        // if the project doesn't contain a *GrailsPlugin.groovy file, it's a grails application
        project.fileTree(dir: project.projectDir, include: '*GrailsPlugin.groovy').isEmpty()
    }

    static ExecResult executeGrailsCommand(Project project, List<String> arguments, boolean interactive, ShowStacktrace stackTrace = ShowStacktrace.INTERNAL_EXCEPTIONS) {
        TCLogger log = new TCLogger(project, Logging.getLogger(GrailsUtils.class))

        String grailsFolder = Path.join(SystemUtils.userHome.path, '.gradlegrails', 'grails', project.grails.version);
        String extension = SystemUtils.IS_OS_WINDOWS ? '.bat' : '';
        String grailsExecutable = Path.join(grailsFolder, 'bin', "grails${extension}");
        log.debug("grails executable: ${grailsExecutable}${extension}")

        Map<String, String> env = new HashMap(System.getenv());
        env['GRAILS_HOME'] = grailsFolder;

        List<String> command = new ArrayList<String>();
        command.add(grailsExecutable);
        command.addAll(arguments);

        if (!interactive) {
            command.add("--non-interactive")
        }
        if(project.hasProperty('grailsVerbose') && project.grailsVerbose){
            command.add("--verbose")
        }else if (project?.gradle?.startParameter?.showStacktrace == ShowStacktrace.ALWAYS) {
            command.add("--stacktrace")
        }

        log.lifecycle("grails command: $command")
        log.debug("env: $env")

        return project.exec {
            commandLine command
            environment env
            standardInput = System.in
        }
    }

}
