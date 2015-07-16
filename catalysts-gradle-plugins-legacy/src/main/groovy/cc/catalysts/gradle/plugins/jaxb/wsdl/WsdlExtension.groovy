package cc.catalysts.gradle.plugins.jaxb.wsdl

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class WsdlExtension {
	List<String> convertList
	
	public WsdlExtension() {
		convertList = []
	}
	
	public convert(convert) {
		convertList.add(convert)
	}
}