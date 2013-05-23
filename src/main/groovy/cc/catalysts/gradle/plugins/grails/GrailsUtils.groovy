package cc.catalysts.gradle.plugins.grails

import com.connorgarvey.gradlegrails.Path
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.Project
import org.gradle.process.ExecResult

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsUtils {

    static boolean isGrailsApplication(Project project) {
        // if the project doesn't contain a *GrailsPlugin.groovy file, it's a grails application
        project.fileTree(dir: project.projectDir, include: '*GrailsPlugin.groovy').isEmpty()
    }

    static ExecResult executeGrailsCommand(Project project, List<String> arguments, boolean interactive) {
        String grailsFolder = Path.join(SystemUtils.userHome.path, '.gradlegrails', 'grails', project.grails.version);
        String extension = SystemUtils.IS_OS_WINDOWS ? '.bat' : '';
        String grailsExecutable = Path.join(grailsFolder, 'bin', "grails${extension}");

        Map<String, String> env = new HashMap(System.getenv());
        env['GRAILS_HOME'] = grailsFolder;

        List<String> command = new ArrayList<String>();
        command.add(grailsExecutable);
        command.addAll(arguments);

        if (!interactive)
            command.add("--non-interactive");

        return project.exec {
            commandLine command
            environment env
            standardInput = System.in
        }
    }

}
