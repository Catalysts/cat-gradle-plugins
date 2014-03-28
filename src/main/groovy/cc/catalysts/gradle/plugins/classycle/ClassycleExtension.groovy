package cc.catalysts.gradle.plugins.classycle

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class ClassycleExtension {
	String definitionFile
	String checkOutputFile
	String graphOutputFile
	String outputDirectory

	ClassycleExtension(){
		definitionFile = "classycle.ddf"
		checkOutputFile = "classycleCheck.xml"
		graphOutputFile = "classycleGraph.xml"
		outputDirectory = "reports"
	}
}
