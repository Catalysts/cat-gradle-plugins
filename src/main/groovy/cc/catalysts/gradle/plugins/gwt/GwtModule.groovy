package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.Named

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class GwtModule implements Named {

    String name
    String modulename

    public GwtModule(String name) {
        this.name = name;
    }

}