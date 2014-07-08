package cc.catalysts.gradle.plugins.codegen.java

import cc.catalysts.gradle.plugins.codegen.CodegenTask
import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.SourceSet

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class JavaGenPlugin implements Plugin<Project> {

    void apply(Project project) {
        private TCLogger log = new TCLogger(project, Logging.getLogger(Task.class))
        project.apply plugin: 'java'

        project.repositories {
            mavenCentral()
        }

        project.configurations {
            compile
            javagen { extendsFrom compile }
            javagenRuntime
        }

        project.ext.velocityVersion = project.hasProperty("velocityVersion") ? project.velocityVersion : '1.7'
        project.ext.cdocletVersion = project.hasProperty("cdocletVersion") ? project.cdocletVersion : '2.0.3'

        project.dependencies {
            javagenRuntime group: 'org.apache.velocity', name: 'velocity', version: project.velocityVersion
            javagen group: 'cc.catalysts', name: 'cdoclet', version: project.cdocletVersion
        }

        project.afterEvaluate {

            project.configurations.javagen.getDependencies().each { depPrj ->
                def prjName = depPrj.getName()

                if (prjName != "cdoclet") {
                    if (project.tasks.findByPath('javagen') == null) {
                        project.tasks.create(name: 'javagen', type: JavaGenTask)
                        project.tasks.compileJava.dependsOn('javagen')
                        if (project.tasks.findByPath('codegen') == null) {
                            project.task('codegen', type: CodegenTask)
                        }
                        project.tasks.codegen.dependsOn('javagen')
                    }

                    def pDir = project.projectDir.canonicalPath
                    def rDir = project.rootProject.projectDir.canonicalPath

                    project.task('javagen-' + prjName) << {

                        log.lifecycle "###Building JAVA for ${project.name} by using ${prjName} [${project.cdocletVersion}]"

                        project.ant.javadoc(
                                classpath: project.configurations.javagen.asPath + ";" + project.configurations.javagenRuntime.asPath + ";" + depPrj.dependencyProject.configurations.compile.asPath,//project.configurations.internal.asPath + ";"
                                sourcepath: rDir + '/' + prjName + '/src/main/java',
                                destdir: new File(pDir + '/target/generated-sources/'),
                                docletpath: project.configurations.javagen.asPath) {

                            doclet(name: "cc.catalysts.cdoclet.CDoclet") {
                                param name: "-generator", value: "java"
                                param name: "-enum", value: "cc.catalysts.cp.annotations.Serialize"
                                //param name:"-namespace", value:"@{namespace}"
                                //param name:"-map", value:"java.io.Serializable:null"
                                param name: "-map", value: "java.lang.Exception:null"
                                param name: "-map", value: "java.security.Principal:null"
                                param name: "-map", value: "java.util.EventListener:null"
                                param name: "-map", value: "java.util.EventObject:null"
                                param name: "-annotationmap", value: "cc.catalysts.cp.annotations.SerializeById:Number"
                                param name: "-annotationmap", value: "cc.catalysts.cp.annotations.DontSerialize:null"
                            }
                        }
                    }

                    project.tasks.javagen.dependsOn(project.tasks.('javagen-' + prjName))

                }
            }
            if (project.tasks.findByPath('javagen') != null) {
                project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
                    if (!sourceSet.name.toLowerCase().contains("test")) {
                        def path = "${project.projectDir}/target/generated-sources"
                        sourceSet.java { srcDir path }
                    }
                }
            }
        }

    }
}