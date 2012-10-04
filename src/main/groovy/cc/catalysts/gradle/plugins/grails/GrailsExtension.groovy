package cc.catalysts.gradle.plugins.grails

import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GrailsExtension {
    Project application
    String version = '2.1.0'
    File warFile // default value depends on project, WarTask handles this
}