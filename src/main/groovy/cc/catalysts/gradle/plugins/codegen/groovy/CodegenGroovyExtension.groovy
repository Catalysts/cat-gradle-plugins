package cc.catalysts.gradle.plugins.codegen.groovy

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class CodegenGroovyExtension {
    String fileExt = "groovy"
    String packageName
    String destDir = "target/generated-sources/main/"

    public packageName(pack) {
        this.packageName = pack
    }

    public destDir(destDir) {
        this.destDir = destDir
    }
}