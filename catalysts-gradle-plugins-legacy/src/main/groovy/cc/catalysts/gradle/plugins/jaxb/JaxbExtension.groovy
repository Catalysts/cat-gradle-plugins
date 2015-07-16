package cc.catalysts.gradle.plugins.jaxb

import org.gradle.api.Project

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class JaxbExtension {
	private Project project
	
	JaxbExtension(Project prj) {
		project = prj
	}
	
	public xsd(Closure closure) {
		closure.delegate = project.extensions.xsd
		closure()
	}
	
	public wsdl(Closure closure) {
		closure.delegate = project.extensions.wsdl
		closure()
	}
}