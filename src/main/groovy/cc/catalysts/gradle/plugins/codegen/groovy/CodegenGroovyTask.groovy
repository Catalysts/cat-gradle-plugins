package cc.catalysts.gradle.plugins.codegen.groovy

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenGroovyTask extends DefaultTask {

    CodegenGroovyTask() {
        project.tasks.codegen.dependsOn(this)
        if (project.tasks.findByName('install-grails')?.dependsOn(this) == null) {
            project.tasks.findByName('compileGroovy')?.dependsOn(this)
        }
    }

    @TaskAction
    def build() {
        def pName = project.codegengroovy.packageName
        if (pName != null) {
            Calendar calendar = Calendar.getInstance()
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm")
            def date = dateFormat.format(calendar.getTime())
            def version = project.version

            File destDir = new File(project.projectDir, project.codegengroovy.destDir)

            String pathToPackage = destDir.getAbsolutePath() + File.separator + pName.replace('.', File.separator)

            new File(pathToPackage).mkdirs()

            def f = new File(pathToPackage, 'Build.' + project.codegengroovy.fileExt)
            if (f.exists()) {
                println "   deleting file [" + f.getAbsolutePath() + "]"
                f.delete()
            }

            println "   writing new file [" + f.getAbsolutePath() + "]"
            def w = f.newWriter()

            w << 'package ' << pName << ';\r\n'
            w << '\r\npublic class Build {\r\n'
            w << '\tpublic static final String VERSION = "' + version + '";\r\n'
            w << '\tpublic static final String DATE = "' + date + '";\r\n'
            w << '}'

            w.close()

        }
    }
}