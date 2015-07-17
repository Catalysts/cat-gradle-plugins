package cc.catalysts.gradle.plugins.deploy

import org.gradle.api.NamedDomainObjectContainer

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class DeployExtension {
    final private NamedDomainObjectContainer<DeployTarget> deploy;

    public DeployExtension(NamedDomainObjectContainer<DeployTarget> deploy) {
        this.deploy = deploy
    }

    public deploy(Closure closure) {
        deploy.configure(closure)
    }
}