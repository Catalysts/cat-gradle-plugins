package cc.catalysts.gradle.plugins.jdeps

import cc.catalysts.gradle.utils.TCLogger
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult

import java.nio.charset.Charset

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
public class JDepsTask extends DefaultTask {
    private TCLogger log = new TCLogger(project, logger)

    public JDepsTask() {
        group = "Code Quality"
        description = "static java8 dependency checker (jdeps)"
        // always regenerate JDeps output files
        outputs.upToDateWhen { false }
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return new File(project.projectDir.absolutePath, project.jdeps.outputDirectory)
    }

    @OutputFile
    public File getOutputFile() {
        return new File(getOutputDirectory(), project.jdeps.outputFile)
    }

    private String changeFileExtension(String file, String extension) {
        if (file ==~ /(?m).*\.([^\.\/\\])+$/) {
            // replace existing file extension
            return file.replaceFirst(/(?m)\.([^\.\/\\])+$/, '.' + extension)
        }
        // just append the new extension
        return file + '.' + extension
    }

    @OutputFile
    public File getDotOutputFile() {
        return new File(getOutputDirectory(), changeFileExtension(project.jdeps.outputFile, 'dot'))
    }

    @OutputFile
    public File getPngOutputFile() {
        return new File(getOutputDirectory(), changeFileExtension(project.jdeps.outputFile, 'png'))
    }

    protected List<String> buildJDepsParameters(String jdepsExe) {
        def args = [jdepsExe]

        String classpath = project.configurations.runtime.resolve().join(System.properties.'path.separator')
        if (project.jdeps.summary) args.add('-summary')
        if (project.jdeps.verbose) args.add('-verbose')
        if (project.jdeps.verboseLevel != null) args.add('-verbose:' + project.jdeps.verboseLevel)
        if (!StringUtils.isEmpty(classpath)) {
            args.add('-classpath')
            args.add('\"\"' + classpath + '\"\"')
        }
        for (String p : project.jdeps.packages) {
            args.add('-package')
            args.add(p)
        }
        if (project.jdeps.regex != null) {
            args.add('-regex')
            args.add(project.jdeps.regex)
        }
        if (project.jdeps.profile) args.add('-profile')
        if (project.jdeps.recursive) args.add('-recursive')

        return args
    }

    protected boolean addArtifacts(List<String> args) {
        boolean added = false
        project.configurations.archives.artifacts.each { def artifact ->
            args.add(artifact.file.absolutePath)
            added = true
        }
        return added
    }

    protected String convertJDepsToDot(String jdeps) {
        def currentUnit = null
        String dotFileString =
                "digraph {\r\n" +
                        jdeps.replaceAll(/(?m)^\S.*(\r)?\n/, '')
                                .replaceAll(/(->\s(\S)+)(.*)/, '$1')
                                .split(/(\r)?\n/)
                                .collect({ line ->
                            if (line ==~ /(?m)^(\s)*[^\s-].*/) {
                                currentUnit = line.replaceFirst(/(?m)^(\s)*([^-](\S)*).*/, '$2')
                                return null
                            }
                            if (currentUnit == null) return null
                            "    " + currentUnit + line.replaceFirst(/(?m)^(\s)*/, ' ')
                        })
                                .findAll({ line ->
                            line != null
                        })
                                .join('\r\n')
                                .replaceAll(/\./, '_') +
                        "\r\n}\r\n"
    }

    protected void writeToFile(File file, String content) {
        OutputStream out = new FileOutputStream(file)
        out.write(content.getBytes(Charset.forName("UTF-8")))
        out.close()
    }

    protected void fetchGraph(String dotFileString) {
        def chartParams = [cht: 'gv', chof: 'png', chl: dotFileString]
        def url = "http://chart.googleapis.com/chart?"
        url += chartParams.collect { k, v -> "$k=${URLEncoder.encode(v)}" }.join('&')
        try {
            getPngOutputFile().withOutputStream { out ->
                out << new URL(url).openStream()
            }
        } catch (Exception ignored) {
            log.warn('Could not fetch JDeps Graph from GoogleAPIs.')
        }
    }

    @TaskAction
    void analyze() {
        String jdepsExe;
        try {
            jdepsExe = getJDepsExecutable();
        } catch (IOException e) {
            log.failure("Unable to find jdeps command: " + e.getMessage(), true)
            //throw new TaskExecutionException(this, new Exception("Unable to find jdeps command: " + e.getMessage(), e));
        }

        def args = buildJDepsParameters(jdepsExe)
        if (!addArtifacts(args)) return

        getOutputDirectory().mkdirs()

        new ByteArrayOutputStream().withStream { os ->
            ExecResult result = project.exec {
                commandLine = args
                standardOutput = os
                ignoreExitValue = true
            }

            String jdepsOutput = os.toString()

            int exitCode = result.getExitValue()
            if (exitCode != 0) {
                log.failure("jdeps exited with code " + exitCode + "\noutput:\n" + jdepsOutput, true)
                //throw new TaskExecutionException(this, new Exception("jdeps exited with code " + exitCode + "\noutput:\n" + jdepsOutput));
            }

            String dotFileString = convertJDepsToDot(jdepsOutput)

            if (project.jdeps.generateDotFile) {
                writeToFile(getDotOutputFile(), dotFileString)
            }

            if (project.jdeps.generateGraph) {
                fetchGraph(dotFileString)
            }

            if (project.jdeps.consolePrint) {
                log.lifecycle("jdeps output:\n" + jdepsOutput)
            }

            writeToFile(getOutputFile(), jdepsOutput)
        }

    }

    private String getJDepsExecutable() {
        String jdepsExe;

        jdepsExe = getJDepsExecutableFromUser();
        if (!StringUtils.isEmpty(jdepsExe)) return jdepsExe;

        jdepsExe = getJDepsExecutableFromSystem();
        if (!StringUtils.isEmpty(jdepsExe)) return jdepsExe;

        jdepsExe = getJDepsExecutableFromEnv();
        if (!StringUtils.isEmpty(jdepsExe)) return jdepsExe;

        log.failure("Could not locate the jdeps executable", true)
    }

    private String getJDepsExecutableFromUser() {
        String jdepsPath = project.jdeps.jdepsPath
        if (StringUtils.isEmpty(jdepsPath)) return null

        File jdepsExe = new File(jdepsPath)
        if (!jdepsExe.exists()) {
            log.failure("The jdeps executable '${jdepsPath}' doesn't exist.", true)
        }

        if (jdepsExe.isDirectory()) {
            String jdepsCommand = "jdeps" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");
            jdepsExe = new File(jdepsExe, jdepsCommand);
        }

        if (!jdepsExe.exists() || !jdepsExe.isFile()) {
            log.failure("The jdeps executable '${jdepsExe}' doesn't exist or is not a file. Verify the jdepsPath setting in your build.gradle", true)
        }

        return jdepsExe.getAbsolutePath()
    }

    private String getJDepsExecutableFromSystem() {
        String javaHome = System.properties.'java.home'
        if (StringUtils.isEmpty(javaHome)) return null
        File javaHomePath = new File(javaHome)
        if (!javaHomePath.exists() || !javaHomePath.isDirectory()) return null
        File jdkPath = javaHomePath;
        if (!SystemUtils.IS_OS_MAC_OSX) {
            jdkPath = javaHomePath.getParentFile()
        }
        if (!jdkPath.exists() || !jdkPath.isDirectory()) return null

        String jdepsCommand = "jdeps" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");
        File jdepsExe = new File(new File(jdkPath, 'bin'), jdepsCommand);

        if (!jdepsExe.exists() || !jdepsExe.isFile()) return null

        log.debug("jdepsExe: ${jdepsExe.getAbsolutePath()}")
        return jdepsExe.getAbsolutePath()
    }

    private String getJDepsExecutableFromEnv() {
        String javaHome = System.env.'JAVA_HOME'
        if (StringUtils.isEmpty(javaHome)) {
            log.failure("The environment variable JAVA_HOME is not correctly set.", true)
        }
        File javaHomePath = new File(javaHome)
        if (!javaHomePath.exists() || !javaHomePath.isDirectory()) {
            log.failure("The environment variable 'JAVA_HOME=${javaHome}' doesn't exist or is not a valid directory.", true)
        }

        String jdepsCommand = "jdeps" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");

        File jdepsExe = new File(new File(javaHomePath, 'bin'), jdepsCommand);

        if (!jdepsExe.exists() || !jdepsExe.isFile()) {
            log.failure("The jdeps executable '${jdepsExe}' doesn't exist or is not a file. Verify the JAVA_HOME environment variable.", true)
        }

        return jdepsExe.getAbsolutePath()
    }

}
