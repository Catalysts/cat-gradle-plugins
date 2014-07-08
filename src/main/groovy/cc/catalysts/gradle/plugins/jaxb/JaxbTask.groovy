package cc.catalysts.gradle.plugins.jaxb

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class JaxbTask extends DefaultTask {

    @TaskAction
    def GenerateAll() {
        /* As this Task depends on all other JaxbGeneration Tasks, everything is done.*/
    }

}