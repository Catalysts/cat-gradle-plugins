package cc.catalysts.gradle.plugins.i18n

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class I18nExtension {
    String pathToProperties
    I18nProperties properties
    List<String> errors
    boolean checkOrder
    List<String> groupsToIgnore

    I18nExtension() {
        properties = new I18nProperties()
        pathToProperties = "grails-app" + File.separatorChar + "i18n"
        errors = []
        checkOrder = false
        groupsToIgnore = []
    }

    void ignore(String name) {
        groupsToIgnore.add(name)
    }
}