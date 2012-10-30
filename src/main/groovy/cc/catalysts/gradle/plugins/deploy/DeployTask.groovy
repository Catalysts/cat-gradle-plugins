package cc.catalysts.gradle.plugins.deploy

import org.gradle.api.DefaultTask
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.process.internal.DefaultJavaExecAction
import org.gradle.process.internal.JavaExecAction

import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry
import java.nio.channels.FileChannel
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import com.jcraft.jsch.Session
import com.jcraft.jsch.JSch
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.Channel

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class DeployTask extends DefaultTask {
    @TaskAction
    def deploy() {
        def usedConfig
        if(project.hasProperty("depConfig")) {
            if(project.deploy.findByName(project.depConfig) != null) {
                usedConfig = project.deploy.findByName(project.depConfig)
            } else {
                println "ERROR: Could not find Deploy-Configuration for '" + project.depConfig + "'!"
                throw new Exception("Deploy-Configuration not defined")
            }
        } else {
            if(project.deploy.size() == 0) {
                println "ERROR: Could not find any Deploy-Configuration!"
                throw new Exception("Deploy-Configuration not defined")
            } else if (project.deploy.size() == 1) {
                usedConfig = project.deploy.iterator().next()
            } else {
                println "ERROR: More than one Deploy-Configurations are defined, please choose one with 'gradle deploy -PdepConfig=confName' where confName is one of the following:"
                for (target in project.deploy) {
                    println "  " + target.name
                }
                throw new Exception("Deploy-Configuration not defined")
            }
        }

        switch(usedConfig.type) {
            case 'lancopy':

                File logDir = new File(usedConfig.webappDir + "\\logs")
                File webappDir = new File(usedConfig.webappDir + "\\webapps")
                File pluginDir = new File(usedConfig.webappDir + "\\plugins")
                File workDir = new File(usedConfig.webappDir + "\\work")

                println "Deploying " + usedConfig.webappWar + " to " + usedConfig.tomcatHost

                println "-- Stopping Tomcat Service.."
                RunCommand(true, ["sc", usedConfig.tomcatHost, "stop", usedConfig.tomcatService])

                deleteDir(logDir)
                deleteDir(webappDir)
                deleteDir(pluginDir)
                deleteDir(workDir)

                logDir.mkdir()

                println '-- Copying ' + usedConfig.webappWar + ' to ' + usedConfig.webappDir
                project.copy {
                    from usedConfig.webappWar
                    into usedConfig.webappDir
                }

                println "-- Starting Tomcat Service.."
                RunCommand(true, ["sc", usedConfig.tomcatHost, "start", usedConfig.tomcatService])

                break
            case 'upload':
                def rootWar = usedConfig.webappWar + "/webapps/ROOT.war"
                def tempDir = System.getProperty('java.io.tmpdir')

                println 'Uploading ' + rootWar + ' to ' + usedConfig.uploadTarget

                deleteDir(new File(tempDir))
                new File(tempDir).mkdir()

                println '  -- Copying ' + rootWar + ' to ' + tempDir
                project.copy {
                    from rootWar
                    into tempDir
                }

                sendFiles(usedConfig, tempDir)

                deleteDir(new File(tempDir))

                break
            default:
                println 'ERROR: invalid Type Property "' + usedConfig.type + '" in Configuration "' + usedConfig.name + '"!'
                throw new Exception("Deploy-Configuration has no type")
        }
    }

    private boolean RunCommand(boolean PrintOutput, ArrayList<String> command) {
        try {
            def sout = new StringBuffer()
            def serr = new StringBuffer()
            ProcessBuilder pb = new ProcessBuilder(command)
            pb.directory(project.file("/"))
            Process proc = pb.start()
            InputStream stdout = proc.getInputStream()
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))
            if(PrintOutput) {
                while((line = reader.readLine ()) != null) {
                    if(line != "") {
                        println (line)
                    }
                }
            }
            proc.waitFor()
            proc.consumeProcessOutput(sout,serr)
            if(proc.exitValue()) {
                println sout
                println serr
                return false
            }
            return true
        } catch (Throwable t) {
            return false
        }
    }

    private void sendFiles(usedConfig, tempDir) {

        Session session = null

        println '  -- Connecting to ' + usedConfig.uploadTarget
        try {
            JSch jsch = new JSch()
            println '    -- getting Session'
            session = jsch.getSession(usedConfig.username, usedConfig.uploadTarget, 22)
            session.setConfig("StrictHostKeyChecking", "no")
            println '    -- set password'
            session.setPassword(usedConfig.password)
            println '    -- connect'
            session.connect()

            File sendDir = new File(tempDir + project.version)
            String[] dirList = sendDir.list()
            if(dirList != null && dirList.length > 0) {
                for(int i=0; i<dirList.length; i++) {
                    File f = new File(sendDir, dirList[i])
                    if(!f.isDirectory()) {
                        println '    -- Sending ' + f.getPath() + ' to ' + usedConfig.uploadDir + '/' + f.getName()
                        def destFilename = usedConfig.uploadDir + '/' + f.getName()
                        def sourceFilename = f.getPath()

                        FileInputStream fis = null

                        String command = "scp -p -t " + destFilename
                        Channel channel = session.openChannel("exec")
                        ((ChannelExec) channel).setCommand(command)

                        OutputStream out = channel.getOutputStream()
                        InputStream inStream = channel.getInputStream()

                        channel.connect()

                        long filesize = (new File(sourceFilename)).length()
                        command = "C0644 " + filesize + " "
                        if (sourceFilename.lastIndexOf('/') > 0) {
                            command += sourceFilename.substring(sourceFilename.lastIndexOf('/') + 1)
                        } else {
                            command += sourceFilename
                        }
                        command += "\n"

                        out.write(command.getBytes())
                        out.flush()

                        fis = new FileInputStream(sourceFilename)
                        byte[] buf = new byte[1024]
                        while (true) {
                            int len = fis.read(buf, 0, buf.length)
                            if (len <= 0) {
                                break
                            }
                            out.write(buf, 0, len)
                        }

                        fis.close()
                        fis = null

                        //send '\0' to end it
                        buf[0] = 0
                        out.write(buf, 0, 1)
                        out.flush()

                        out.close()

                        channel.disconnect()
                    }
                }
            }
            session.disconnect()
            session = null
        } catch (Throwable t) {
            println '  -- Connection failed! ' + t.toString()
        }
        if(session != null) {
            session.disconnect()
        }
    }

    private static boolean deleteDir(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles()
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDir(files[i])
                }
                else {
                    files[i].delete()
                }
            }
        }
        return path.delete()
    }
}