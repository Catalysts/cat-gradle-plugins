package cc.catalysts.gradle.plugins.querydsl

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class QueryDslExtension {
    String destinationDir

    QueryDslExtension(){
        destinationDir = "target" + File.separatorChar + "generated-sources" + File.separatorChar + "querydsl"
    }
}