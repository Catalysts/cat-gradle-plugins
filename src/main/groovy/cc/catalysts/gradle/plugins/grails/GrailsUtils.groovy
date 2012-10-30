package cc.catalysts.gradle.plugins.grails

import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsUtils {

    static boolean isGrailsApplication(Project project) {
        // if the project doesn't contain a *GrailsPlugin.groovy file, it's a grails application
        project.fileTree(dir: project.projectDir, include: '*GrailsPlugin.groovy').isEmpty()
    }

}
