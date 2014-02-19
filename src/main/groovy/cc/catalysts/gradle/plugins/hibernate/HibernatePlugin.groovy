package cc.catalysts.gradle.plugins.hibernate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile;

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class HibernatePlugin implements Plugin<Project> {

    void apply(Project project) {
        project.apply plugin: 'java'

        project.extensions.hibernate = new HibernateExtension()

        project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
            if (!sourceSet.name.toLowerCase().contains("test")) {
                sourceSet.java { srcDir project.hibernate.destinationDir }
            }
        }

        Task clean = project.task("cleanHibernate", type: CleanHibernateTask, description: 'Cleans output directory of Hibernate', group: "Hibernate")
        project.tasks.getByName('clean').dependsOn(clean)

        Task create = project.task("createHibernateOut", dependsOn: clean, type: HibernateOutputDirTask, description: 'Creates output directory of Hibernate', group: "Hibernate")

        if (!project.hasProperty("hibernateVersion")) {
            project.ext.hibernateVersion = '1.2.0.Final'
        }

        project.configurations {
            jpamodelgen
        }

        project.dependencies {
            jpamodelgen group: 'org.hibernate', name: 'hibernate-jpamodelgen', version: project.hibernateVersion
        }

        Task generateMetamodel = project.task("generateHibernateMetamodel", dependsOn: create, type: JavaCompile, description: 'Creates Hibernate JPA Model Sources', group: "Hibernate") {
            source = project.convention.plugins.java.sourceSets.main.java
            classpath = project.configurations.compile + project.configurations.jpamodelgen
            options.compilerArgs = [
                "-proc:only"
            ]
            destinationDir = new File(project.projectDir.absolutePath, project.hibernate.destinationDir)
        }

        project.compileJava {
            dependsOn generateMetamodel
        }

    }
}
