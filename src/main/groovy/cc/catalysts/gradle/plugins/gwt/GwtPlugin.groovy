package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GwtPlugin implements Plugin<Project> {

    public static final String BUILD_GWT_CONFIGURATION_NAME = "gwtBuild";
    public static final String COMPILE_GWT_CONFIGURATION_NAME = "gwtCompile";


    void apply(Project project) {
        project.apply plugin: 'java'
        project.apply plugin:  'eclipse'

        project.repositories {
            mavenCentral()
        }

        project.extensions.gwt = new GwtExtension(project.container(GwtModule))

        if(!project.hasProperty("gwtVersion")) {
            project.ext.gwtVersion = "2.4.0"
        }

        configureConfigurations(project.getConfigurations());

        project.artifacts {
            gwtCompile project.jar
        }

        project.dependencies {
            gwtBuild (
                [group: 'com.google.gwt', name: 'gwt-dev', version: project.gwtVersion]
            )
            gwtCompile (
             	[group: 'com.google.gwt', name: 'gwt-user', version: project.gwtVersion]
            )
        }

        project.eclipse {
            classpath {
                plusConfigurations += project.configurations.gwtBuild
                plusConfigurations += project.configurations.gwtCompile
                noExportConfigurations += project.configurations.gwtBuild
            }
        }

        // we bundle sources in GWT modules
        project.jar {
            from project.sourceSets.main.allJava
        }

        project.task('cleanGwt', type: CleanGwtTask, group: 'Gwt')
        project.task('compileGwt', type: CompileGwtTask, group: 'Gwt')
        project.task('generateLaunchConfig', type: EclipseLaunchConfigGwtTask, group: 'Gwt')

        if (project.plugins.findPlugin('war')) {
            project.war.dependsOn 'compileGwt'
        }

        // define source folders
        project.sourceSets {
            main {
                java {
                    srcDir 'src/main/java'
                }
                resources {
                    srcDir 'src/main/java'
                }
            }
        }
    }

    public void configureConfigurations(ConfigurationContainer configurationContainer) {
        Configuration compileGwtConfiguration = configurationContainer.create(COMPILE_GWT_CONFIGURATION_NAME).setVisible(false).
                setDescription("Libraries that are required for GWT compilation");
        Configuration buildGwtConfiguration = configurationContainer.create(BUILD_GWT_CONFIGURATION_NAME).setVisible(false).
                setDescription("Libraries that are required for the GWT compiler at runtime.");
        configurationContainer.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(compileGwtConfiguration)
    }
}
