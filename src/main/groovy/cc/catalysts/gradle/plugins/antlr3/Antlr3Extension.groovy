package cc.catalysts.gradle.plugins.antlr3

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class Antlr3Extension {
    List<String> grammarList
    String destinationDir
    String antlrSource

    Antlr3Extension(){
        grammarList = []
        antlrSource = "src" + File.separatorChar + "main" + File.separatorChar + "antlr"
        destinationDir = "target" + File.separatorChar + "generated-sources" + File.separatorChar + "antlr"
    }

    public generate(String grammar){
        grammarList.add(grammar)
    }
}