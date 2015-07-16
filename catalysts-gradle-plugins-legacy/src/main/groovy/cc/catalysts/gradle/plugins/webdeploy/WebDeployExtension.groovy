package cc.catalysts.gradle.plugins.webdeploy

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class WebDeployExtension {

    String destination
    String privateKeyPath
    String productionConfiguration
    Boolean onlyModifiedFiles = true
    List<String> excludes = []

    void exclude(String exclude) {
        excludes.add(exclude)
    }

}
