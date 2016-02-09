package cc.catalysts.gradle.buildinfo.task

import cc.catalysts.gradle.buildinfo.BuildInfoExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * @author Klaus Lehner
 */
class BuildInfoTask extends DefaultTask {

    @TaskAction
    void generateBuildInfo() {
        BuildInfoExtension config = project.buildinfo;

        File buildInfoDir = new File(config.destinationDir, "${config.packageName.replace('.', '/')}")
        buildInfoDir.mkdirs()
        File buildInfoFile = new File(buildInfoDir, 'BuildInfo.java')
        if (buildInfoFile.exists()) {
            buildInfoFile.delete()
        }
        Date buildTime = new Date();
        buildInfoFile.write("""package ${config.packageName};

public final class BuildInfo {
    private BuildInfo() {}
    public static final String VERSION = "${project.version}";
    public static final String BUILD_TIME = "${buildTime.format("dd.MM.yyyy HH:mm:ss")}";
    public static final long BUILD_TIMESTAMP = ${buildTime.getTime()}L;
}
""")

    }
}
