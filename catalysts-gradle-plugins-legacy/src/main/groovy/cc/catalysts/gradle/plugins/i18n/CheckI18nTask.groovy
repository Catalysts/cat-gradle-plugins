package cc.catalysts.gradle.plugins.i18n

import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static cc.catalysts.gradle.plugins.i18n.I18nPropertyFileGroup.getPropertyFileName
import static cc.catalysts.gradle.plugins.i18n.I18nPropertyFileGroup.isPropertyFile

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class CheckI18nTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    private I18nExtension e

    @TaskAction
    def check() {
        e = project.extensions.cat_i18n

        String rootPath = project.getProjectDir().getAbsolutePath() + File.separator + e.getPathToProperties()
        File root = new File(rootPath)

        if (root == null || !root.exists() || !root.isDirectory()) {
            log.failure("Could not find directory: ${rootPath}", true)
        }

        // create properties file list if empty
        if (e.getProperties() == null || e.getProperties().isEmpty()) {
            createFileList(root);
        }

        for (I18nPropertyFileGroup g : e.getProperties().getGroups()) {
            g.createMasterTemplate(root)

        }

        for (I18nPropertyFileGroup g : e.getProperties().getGroups()) {
            g.verify(root, e)
        }

        if (e.getErrors() != null && e.getErrors().size() > 0) {
            String LF = System.getProperty("line.separator"); ;
            StringBuilder errorMsg = new StringBuilder()
            errorMsg.append("The following errors occured: ")
            for (String error : e.getErrors()) {
                errorMsg.append(LF)
                errorMsg.append(error)
            }
            log.failure(errorMsg.toString(), true)
        }
    }

    private void createFileList(File root) {
        for (File f : root.listFiles()) {
            if (isPropertyFile(f.getName())) {
                String name = getPropertyFileName(f.getName());
                if (e.groupsToIgnore.contains(name)) {
                    log.lifecycle("Ignoring file '${f.getName()}'")
                    continue
                }
                I18nPropertyFileGroup curGroup = e.getProperties().hasGroup(name)
                if (curGroup == null) {
                    log.lifecycle("Creating property file group '${name}' for file '${f.getName()}'")
                    curGroup = new I18nPropertyFileGroup(f.getName())
                    e.getProperties().addGroup(curGroup)
                } else {
                    log.lifecycle("Adding property file '${f.getName()}' to group '${name}'")
                    curGroup.addFile(f.getName())
                }
            }
        }
    }
}