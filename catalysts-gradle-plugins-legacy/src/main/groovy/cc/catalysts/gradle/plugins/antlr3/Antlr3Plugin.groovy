package cc.catalysts.gradle.plugins.antlr3

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class Antlr3Plugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: 'java'

        project.extensions.antlr3 = new Antlr3Extension()

        project.configurations {
            antlr3
        }

        project.dependencies {
            compile 'org.antlr:antlr-runtime:3.4'
            antlr3 'org.antlr:antlr:3.4'

            testCompile 'junit:junit:4.10'
        }

        project.sourceSets {
            main {
                java {
                    srcDir project.antlr3.destinationDir
                }
            }
        }

        Task clean = project.task("cleanAntlr3", type: CleanAntlr3Task, description: 'Cleans output directory of antlr3',group: "Antlr3")
        project.tasks.getByName('clean').dependsOn(clean)
        Task create = project.task("createAntlr3Out", type: Antlr3OutputDirTask, dependsOn: clean, description: 'Creates output directory of antlr3',group: "Antlr3")
        Task generate = project.task("generateGrammarSource", type: GenerateGrammarSourceTask, dependsOn: create, description: "Generates Java sources from Antlr3 grammars.", group: "Antlr3")
        project.tasks.getByName('compileJava').configure {
            dependsOn generate
            source project.antlr3.destinationDir
        }
    }
}