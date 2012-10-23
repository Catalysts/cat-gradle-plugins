package cc.catalysts.gradle.plugins.jaxb

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import cc.catalysts.gradle.plugins.jaxb.xsd.XsdPlugin
import cc.catalysts.gradle.plugins.jaxb.wsdl.WsdlPlugin
import cc.catalysts.gradle.plugins.codegen.CodegenTask

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class JaxbPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.plugins.apply(JavaPlugin)
		project.task('codegen', type: CodegenTask)
		
        project.configurations.add('jaxb') {
            visible = false
            transitive = false
            description = "The JAXB libraries to be used for this project."
        }
		
        project.configurations.compile {
            extendsFrom project.configurations.jaxb
        }
		
		project.ext.jaxbVersion = project.hasProperty("jaxbVersion") ? project.jaxbVersion : '2.1'
		project.ext.jaxbApiVersion = project.hasProperty("jaxbApiVersion") ? project.jaxbApiVersion : '2.1'
		project.ext.axisVersion = project.hasProperty("axisVersion") ? project.axisVersion : '1.4'
		
		project.dependencies {
			jaxb 'com.sun.xml.bind:jaxb-xjc:' + project.jaxbVersion
			jaxb 'com.sun.xml.bind:jaxb-impl:' + project.jaxbVersion
			jaxb 'javax.xml.bind:jaxb-api:' + project.jaxbApiVersion
			jaxb 'axis:axis-ant:' + project.axisVersion
			jaxb 'axis:axis:' + project.axisVersion
		}
		
		def jaxbTask = project.tasks.add("jaxb", JaxbTask)
		jaxbTask.group = GenerateJaxbTask.GENERATE_GROUP
		jaxbTask.description = "Generates code from all JAXB sources."
		
		project.plugins.apply(XsdPlugin)
		project.plugins.apply(WsdlPlugin)
		
		project.extensions.jaxb = new JaxbExtension(project)
    }

    public static boolean dirIsEmpty(File dir) {
        if(!dir.isDirectory()) {
            return true
        }
        String[] files = dir.list()
        return files.length == 0
    }
}
