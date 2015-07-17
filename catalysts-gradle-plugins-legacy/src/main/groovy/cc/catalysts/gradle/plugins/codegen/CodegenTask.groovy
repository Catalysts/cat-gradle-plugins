package cc.catalysts.gradle.plugins.codegen

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class CodegenTask extends DefaultTask {
	
   @TaskAction
   def Generate() {
	   /* As this Task depends on all other generate Tasks, everything is done.*/
   }
   
}