package cc.catalysts.gradle.plugins.codegen.java

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class CodegenJavaExtension {
	String packageName
	List<String> destDirs = []
	
	public packageName(pack) {
		this.packageName = pack
	}
	
	public build(Closure closure) {
		closure(this)
	}
}