package cc.catalysts.gradle.plugins.codegen.as

import org.gradle.api.Project
import org.gradle.api.Plugin
import cc.catalysts.gradle.plugins.codegen.CodegenTask

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class AsGenPlugin implements Plugin<Project> {
	
	void apply(Project project) {
		project.apply plugin: 'cp-flex'
		
		project.repositories {
			mavenCentral()
			ivy {
				url project.repositoryUrl + "/" + project.repositoryGradle
				layout 'maven'
			}
		}
		
		project.configurations {
			compile
			asgen {extendsFrom compile}
			asgenRuntime
		}
		
		project.ext.velocityVersion = project.hasProperty("velocityVersion") ? project.velocityVersion : '1.7'
		project.ext.cdocletVersion = project.hasProperty("cdocletVersion") ? project.cdocletVersion : '2.0.2'
		project.ext.metaasVersion = project.hasProperty("metaasVersion") ? project.metaasVersion : '0.9'
		
		project.dependencies {
			asgenRuntime group: 'org.apache.velocity', name: 'velocity', version: project.velocityVersion
			asgen group: 'cc.catalysts', name: 'metaas', version: project.metaasVersion
			asgen group: 'cc.catalysts', name: 'cdoclet', version: project.cdocletVersion
		}
		
		project.afterEvaluate {
			
			project.configurations.asgen.getDependencies().each { depPrj ->
				def prjName = depPrj.getName()
				
				if(prjName != "cdoclet" && prjName != "metaas") {
					if(project.tasks.findByPath('asgen') == null) {
						project.tasks.add(name: 'asgen', type: AsGenTask)
						project.tasks.build.dependsOn('asgen')
						if(project.tasks.findByPath('codegen') == null) {
							project.task('codegen', type: CodegenTask)
						}
						project.tasks.codegen.dependsOn('asgen')
					}
					
					def pDir = project.projectDir.canonicalPath
					def rDir = project.rootProject.projectDir.canonicalPath
					
					project.task ('asgen-' + prjName) << {
						
						println "### Building AS for " + project.name + " by using " + prjName
						
						project.ant.javadoc (
							classpath: project.configurations.asgen.asPath + ";" + project.configurations.asgenRuntime.asPath + ";" + project.configurations.internal.asPath + ";" + depPrj.dependencyProject.configurations.compile.asPath,
							sourcepath: rDir + '/' + prjName + '/src',
							destdir: new File(pDir + '/target/generated-sources/'),
							docletpath: project.configurations.asgen.asPath) {
							
							doclet(name: "cc.catalysts.cdoclet.CDoclet") {
								param name:"-generator", value:"actionscript"
								param name:"-enum", value:"cc.catalysts.cp.annotations.Serialize"
								//param name:"-namespace", value:"@{namespace}"
								param name:"-map", value:"java.io.Serializable:null"
								param name:"-map", value:"java.lang.Exception:null"
								param name:"-map", value:"java.security.Principal:null"
								param name:"-map", value:"java.util.EventListener:null"
								param name:"-map", value:"java.util.EventObject:null"
								param name:"-annotationmap", value:"cc.catalysts.cp.annotations.SerializeById:Number"
								param name:"-annotationmap", value:"cc.catalysts.cp.annotations.DontSerialize:null"
							}
						}
					}
					
					project.tasks.asgen.dependsOn(project.tasks.('asgen-' + prjName))
					
				}
			}
			if(project.tasks.findByPath('asgen') != null) {
				project.srcDirs.add('target/generated-sources')
			}
		}
		
	}
}