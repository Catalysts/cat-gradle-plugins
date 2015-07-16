package cc.catalysts.gradle.plugins.codegen.groovy

import cc.catalysts.gradle.plugins.codegen.java.CodegenJavaExtension
import cc.catalysts.gradle.utils.TCLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class CodegenGroovyTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)
    private CodegenGroovyExtension extension = project.codegengroovy

    CodegenGroovyTask() {
        project.tasks.codegen.dependsOn(this)
        if (project.tasks.findByName('install-grails')?.dependsOn(this) == null) {
            project.tasks.findByName('compileGroovy')?.dependsOn(this)
        }
    }

    @TaskAction
    def build() {
        def pName = extension.packageName
        if (pName != null) {
            Calendar calendar = Calendar.getInstance()
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm")
            def date = dateFormat.format(calendar.getTime())
            String version = project.version as String
            if (version.equals("unspecified")) {
                version = project.parent.version
                if (version.equals("unspecified")) {
                    log.warn("project.version is \"unspecified\"")
                }
            }

            File destDir = new File(project.projectDir, extension.destDir)

            String pathToPackage = destDir.getAbsolutePath() + File.separator + pName.replace('.', File.separator)

            new File(pathToPackage).mkdirs()

            def f = new File(pathToPackage, 'Build.' + extension.fileExt )
            if (f.exists()) {
                log.lifecycle "Deleting file '${f.getAbsolutePath()}'"
                f.delete()
            }

            log.lifecycle "Writing new file '${f.getAbsolutePath()}'"
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