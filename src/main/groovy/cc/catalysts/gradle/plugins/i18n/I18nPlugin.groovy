package cc.catalysts.gradle.plugins.i18n

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class I18nPlugin implements Plugin<Project> {
    void apply(Project project) {

        project.extensions.cat_i18n = new I18nExtension()

        project.tasks.create("check-i18n", CheckI18nTask)
    }
}