package cc.catalysts.gradle

class GradleHelper {
    static boolean canBeResolved(configuration) {
        // Configuration.isCanBeResolved() has been introduced with Gradle 3.3,
        // thus we need to check for the method's existence first
        configuration.metaClass.respondsTo(configuration, "isCanBeResolved") ?
                configuration.isCanBeResolved() : true
    }
}
