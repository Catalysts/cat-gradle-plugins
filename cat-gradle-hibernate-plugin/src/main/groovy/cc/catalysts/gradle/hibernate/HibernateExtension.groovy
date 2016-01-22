package cc.catalysts.gradle.hibernate

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class HibernateExtension {
    String destinationDir

    HibernateExtension(){
        destinationDir = "target" + File.separatorChar + "generated-sources" + File.separatorChar + "hibernate"
    }
}