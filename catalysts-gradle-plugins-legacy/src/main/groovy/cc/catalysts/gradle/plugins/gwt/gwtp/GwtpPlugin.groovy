package cc.catalysts.gradle.plugins.gwt.gwtp

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.SourceSet

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GwtpPlugin implements Plugin<Project> {

    void apply(Project project) {

        if(project.tasks.findByPath('cleanTarget') == null) {
            project.task('cleanTarget') << {
                new File(project.projectDir.absolutePath + '/target').deleteDir()
            }
            project.tasks.clean.dependsOn('cleanTarget');
        }

        if(project.tasks.findByPath('createTarget') == null) {
            project.task('createTarget') << {
                new File(project.projectDir.absolutePath + '/target/generated-sources/gwtp').mkdirs();
            }
            project.tasks.compileJava.dependsOn('createTarget');
        }

        project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
            if(!sourceSet.name.toLowerCase().contains("test")) {
                sourceSet.java { srcDir 'target/generated-sources/gwtp' }
            }
        }

        project.jar {
            from project.projectDir.absolutePath + '/target/generated-sources/gwtp'
        }

        project.compileJava {
            options.compilerArgs = [
                    '-processor', 'com.gwtplatform.dispatch.annotation.processor.GenEventProcessor',
                    '-s', project.projectDir.absolutePath + '/target/generated-sources/gwtp'
            ]
        }
    }
}