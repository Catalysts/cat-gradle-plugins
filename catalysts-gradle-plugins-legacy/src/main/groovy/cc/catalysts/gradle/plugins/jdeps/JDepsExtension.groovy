package cc.catalysts.gradle.plugins.jdeps

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class JDepsExtension {

	boolean      consolePrint    = false
	boolean      generateDotFile = false
	boolean      generateGraph   = false
	String       jdepsPath       = null
	String       outputFile      = "jdeps.txt"
	String       outputDirectory = "reports"
	List<String> packages        = []
	boolean      profile         = false
	boolean      recursive       = false
	String       regex           = null
	boolean      summary         = false
	boolean      verbose         = false
	String       verboseLevel    = null

}
