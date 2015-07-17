package cc.catalysts.gradle.plugins.sass

import cc.catalysts.gradle.plugins.css.PreprocessorTask
import org.gradle.api.tasks.TaskAction

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class SassTask extends PreprocessorTask {

    @TaskAction
    void compile() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("jruby")
        engine.put("sourceFiles", sourceFiles.getFiles().collectEntries { File sourceFile ->
            return [sourceFile.path, new File(outputDirectory, sourceFile.name.replaceAll(/\.(sass|scss)$/, ".css")).path]
        })

        engine.eval('''
            require 'rubygems'
            require 'sass'

            $sourceFiles.each do |src, dest|
                sass_engine = Sass::Engine.for_file(src, {:load_paths => [File.dirname(src)]})
                File.open(dest, 'w') do |file|
                    file.write sass_engine.render
                end
            end
''')
    }
}
