package cc.catalysts.gradle.plugins.codegen.java

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class CodegenJavaExtension {
    String packageName
    String destDir = "target/generated-sources/main/"

    public packageName(packageName) {
        this.packageName = packageName
    }

    public destDir(destDir) {
        this.destDir = destDir
    }
}