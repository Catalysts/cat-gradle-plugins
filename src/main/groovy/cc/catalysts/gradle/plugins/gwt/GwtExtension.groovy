package cc.catalysts.gradle.plugins.gwt

import org.gradle.api.NamedDomainObjectContainer

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class GwtExtension {

    final private NamedDomainObjectContainer<GwtModule> modules;
    private String style
    private String workers
    private String warFolder

    public GwtExtension(NamedDomainObjectContainer<GwtModule> modules) {
        this.modules = modules
        style = 'OBF'
        workers = '1'
        warFolder = 'src/main/webapp'
        codeSrvStartupUrl = 'http://localhost:8888/'
    }

    public modules(Closure closure) {
        modules.configure(closure)
    }

    public style(style) {
        this.style = style
    }

    private String codeSrvStartupUrl

    public codeSrvStartupUrl(codeSrvStartupUrl) {
        this.codeSrvStartupUrl = codeSrvStartupUrl
    }

    public build(Closure closure) {
        closure(this)
    }
}