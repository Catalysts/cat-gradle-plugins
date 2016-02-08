package cc.catalysts.gradle.webjars.task

import cc.catalysts.gradle.webjars.WebjarsExtension
import org.gradle.api.Incubating
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException

import java.nio.file.Files

/**
 * This task is still incubating and may change completely an a future version!
 *
 * @author Thomas Scheinecker, Catalysts GmbH
 */
@Incubating
class WebjarsClass extends AbstractWebjarAware {

    String packageName

    WebjarsClass() {
        group = 'Cat-Boot Webjars'
        description = 'Creates a \'Webjars.java\' file which holds a Map with information about you webjars'
    }

    String getPackageName() {
        return packageName ?: config.packageName
    }

    @TaskAction
    void generateWebjarsClass() {
        WebjarsExtension config = project.webjars;

        File webjarsClassDir = new File(config.destinationDir, "${getPackageName().replace('.', '/')}")
        Files.createDirectories(webjarsClassDir.toPath());

        StringBuilder webjarClassBuilder = new StringBuilder("""package ${getPackageName()};

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class Webjars {
    public static class Webjar {
        public final String group;
        public final String name;
        public final String version;
        public final String path;

        private Webjar(String group, String name, String version) {
            this.group = group;
            this.name = name;
            this.version = version;
            this.path = "webjars/" + name + "/" + version;
        }
    }

    public static final Map<String, Webjar> webjars;

    static {
""");

        webjarClassBuilder.append("        Map<String, Webjar> webjarsMap = new HashMap<String, Webjar>();\n")

        for (ResolvedArtifact webjar : webjars) {
            ModuleVersionIdentifier id = webjar.moduleVersion.id

            String webjarConstructor = /new Webjar("${id.group}", "${id.name}", "${id.version}")/;
            webjarClassBuilder.append("        webjarsMap.put(\"${id.name}\", ${webjarConstructor});\n")
        }
        webjarClassBuilder.append("        webjars = Collections.unmodifiableMap(webjarsMap);\n")
        webjarClassBuilder.append("    }\n");
        webjarClassBuilder.append("}");


        File webjarClassJavaFile = new File(webjarsClassDir, 'Webjars.java')
        webjarClassJavaFile.text = webjarClassBuilder.toString()

        if (!webjarClassJavaFile.exists()) {
            throw new TaskConfigurationException(path, "Couldn't create ${webjarClassJavaFile}!", null)
        }

        logger.lifecycle("Successfully created ${webjarClassJavaFile}")

    }

}
