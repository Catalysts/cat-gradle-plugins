package cc.catalysts.gradle.plugins.grails

import org.gradle.api.Project

class GrailsExtension {
    Project application
    String version = '2.1.0'
    File warFile // default value depends on project, WarTask handles this
}