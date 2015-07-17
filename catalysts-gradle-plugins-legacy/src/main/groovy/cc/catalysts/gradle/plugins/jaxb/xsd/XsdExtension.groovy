package cc.catalysts.gradle.plugins.jaxb.xsd

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class XsdExtension {
	List<String> convertList
	
	public XsdExtension() {
		convertList = []
	}
	
	public convert(convert) {
		convertList.add(convert)
	}
}