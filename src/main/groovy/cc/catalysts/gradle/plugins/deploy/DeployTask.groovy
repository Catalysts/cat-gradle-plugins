package cc.catalysts.gradle.plugins.deploy

import cc.catalysts.gradle.plugins.FileDeleteException
import cc.catalysts.gradle.plugins.FileUploadException
import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static cc.catalysts.gradle.utils.TCUtiles.error
import static cc.catalysts.gradle.utils.TCUtiles.warn

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class DeployTask extends DefaultTask {
    @TaskAction
    def deploy() {
        def usedConfig
        if (project.hasProperty("depConfig")) {
            if (project.deploy.findByName(project.depConfig) != null) {
                usedConfig = project.deploy.findByName(project.depConfig)
            } else {
                error(project, logger, "Could not find Deploy-Configuration for '${project.depConfig}'!")
                throw new Exception("Deploy-Configuration not defined")
            }
        } else {
            if (project.deploy.size() == 0) {
                error(project, logger, "Could not find any Deploy-Configuration!")
                throw new Exception("Deploy-Configuration not defined")
            } else if (project.deploy.size() == 1) {
                usedConfig = project.deploy.iterator().next()
            } else {
                error(project, logger, "More than one Deploy-Configurations are defined, please choose one with 'gradle deploy -PdepConfig=confName' where confName is one of the following:")
                for (target in project.deploy) {
                    error(project, logger, "  ${target.name}")
                }
                throw new Exception("Deploy-Configuration not defined")
            }
        }

        switch (usedConfig.type) {
            case 'lancopy':
                File logDir = new File(usedConfig.webappDir as String, "logs")
                File webappDir = new File(usedConfig.webappDir as String, "webapps")
                File pluginDir = new File(usedConfig.webappDir as String, "plugins")
                File workDir = new File(usedConfig.webappDir as String, "work")

                logger.lifecycle "Deploying ${usedConfig.webappWar} to ${usedConfig.tomcatHost}"
                logger.lifecycle "Stopping Tomcat Service.."

                String state = executeServiceCommand(["sc", usedConfig.tomcatHost, "stop", usedConfig.tomcatService])

                int i = 1;
                int maxChecks = 5;
                while (state.equals("STOP_PENDING")) {
                    warn(project, logger, "Tomcat didn't stop, check again in 5s ${i}/${maxChecks} ...")
                    i++
                    if (i > maxChecks) {
                        throw new Exception("Couldn't stop tomcat service")
                    }
                    Thread.sleep(5000)
                    state = executeServiceCommand(["sc", usedConfig.tomcatHost, "query", usedConfig.tomcatService])
                }
                logger.debug "Tomcat State: ${state}"
                logger.lifecycle "Waiting 5s for file handle release"
                Thread.sleep(5000)

                def folders = [logDir, webappDir, pluginDir, workDir]

                for (def folder : folders) {
                    if (deleteDir(folder)) {
                        logger.lifecycle "Successfully deleted ${folder.path}"
                    } else {
                        error(project, logger, "Could not delete ${folder.path}")
                    }
                }

                if (logDir.mkdir()) {
                    logger.debug "Created '${logDir.path}' folder"
                }

                logger.lifecycle "Copying '${usedConfig.webappWar}' to '${usedConfig.webappDir}'"

                project.copy {
                    from usedConfig.webappWar
                    into usedConfig.webappDir
                }

                logger.lifecycle "Starting Tomcat Service.."
                executeServiceCommand(["sc", usedConfig.tomcatHost, "start", usedConfig.tomcatService])

                break
            case 'upload':
                def webappDir = usedConfig.webappDir

                logger.lifecycle "Uploading '${webappDir}' to '${usedConfig.uploadTarget}'"

                sendFiles(usedConfig, webappDir as String)

                break
            default:
                error(project, logger, "invalid Type Property '${usedConfig.type}' in Configuration '${usedConfig.name}'!")
                throw new Exception("Deploy-Configuration has no type")
        }
    }

    private String executeServiceCommand(List<String> command) {
        logger.debug "Executing: ${command}"
        Process proc = command.execute()
        proc.waitFor()

        String stdout = proc.in.text
        logger.info "stderr: ${proc.err.text}"
        logger.info "stdout: ${stdout}"
        logger.info "return code: ${proc.exitValue()}"

        for (String line : stdout.split("\n")) {
            logger.debug "process read: ${line}"
            if (line.contains("STATE")) {
                String[] spl = line.split("\\s+")
                logger.debug "Tomcat Status: ${spl[4]}"
                return spl[4]
            }
        }

        return ""
    }

    private void sendFiles(usedConfig, String webappDir) {

        Session session = null

        logger.lifecycle "Connecting to '${usedConfig.uploadTarget}'"
        JSch jsch = new JSch()
        logger.lifecycle "getting session ..."
        session = jsch.getSession(usedConfig.username as String, usedConfig.uploadTarget as String, 22)
        session.setConfig("StrictHostKeyChecking", "no")
        logger.lifecycle "setting password ..."
        session.setPassword(usedConfig.password as String)
        logger.lifecycle "connecting"
        session.connect()
        session.sendKeepAliveMsg()


        String baseUploadDir = usedConfig.uploadDir + '/' + project.version

        File sendDir = new File(webappDir)
        File[] dirList = sendDir.listFiles()
        if (dirList.size() == 0) {
            warn(project, logger, "webApp directory is empty - no files to upload")
        } else {
            createRemoteDir(session, baseUploadDir)

            uploadDir(session, usedConfig, dirList, baseUploadDir)
        }
        logger.lifecycle "Finished"
        session.disconnect()
        session = null
    }

    private void uploadDir(Session session, def usedConfig, File[] dirList, String directory) {
        if (dirList == null || session == null || directory == null || usedConfig == null) {
            return;
        }
        for (int i = 0; i < dirList.length; i++) {
            File f = dirList[i]
            if (f.isDirectory()) {
                String newDir = directory + '/' + f.getName()
                createRemoteDir(session, newDir)
                uploadDir(session, usedConfig, f.listFiles(), newDir)
            } else {
                uploadSshFile(session, f, f.getPath(), directory + '/' + f.getName())
            }
        }
    }

    private void uploadSshFile(Session session, File f, String sourceFilename, String destFilename) {
        String command = null
        OutputStream out = null
        InputStream inStream = null
        Channel channel = null

        long fileSize = f.length()

        logger.lifecycle "Sending '${sourceFilename}' to '${destFilename}' (~ ${(long) (fileSize / 1024)} KB)"

        FileInputStream fis = null

        // SCP UPLOAD
        command = "scp -p -t '" + destFilename + "'"
        channel = session.openChannel("exec")
        ((ChannelExec) channel).setCommand(command)

        out = channel.getOutputStream()
        inStream = channel.getInputStream()

        channel.connect()

        logger.debug "Executing $command"

        if (checkAck(inStream) != 0) {
            throw new FileUploadException(sourceFilename);
        } else {
            logger.debug "Acknowledge successful"
        }

        // create File and upload
        command = "C0644 " + fileSize + " "
        if (sourceFilename.lastIndexOf('/') > 0) {
            command += sourceFilename.substring(sourceFilename.lastIndexOf('/') + 1)
        } else {
            command += sourceFilename
        }
        command += "\n"

        logger.debug "Executing $command"

        out.write(command.getBytes())
        out.flush()

        if (checkAck(inStream) != 0) {
            throw new FileUploadException(sourceFilename);
        } else {
            logger.debug "Acknowledge successful"
        }

        fis = new FileInputStream(sourceFilename)
        boolean showProgress = false
        byte[] buf = new byte[1024]
        int sendBuffer = 0
        long fivePercent = fileSize / 20
        int completed = 0
        if (fileSize > 1024 * 50) {
            showProgress = true
            System.out.print "           \"|\" = 5% ["
            System.out.flush()
        }
        while (true) {
            int len = fis.read(buf, 0, buf.length)
            if (len <= 0) {
                break
            }
            out.write(buf, 0, len)
            sendBuffer += buf.size()
            if (showProgress && sendBuffer > fivePercent) {
                // 5% sended
                sendBuffer = sendBuffer - fivePercent
                completed += 5
                if (completed == 25) {
                    System.out.print '25%'
                } else if (completed == 50) {
                    System.out.print '50%'
                } else if (completed == 75) {
                    System.out.print '75%'
                } else if (completed == 100) {
                    System.out.print '100%'
                } else {
                    System.out.print '|'
                }
                System.out.flush()
            }
        }
        out.flush()
        fis.close()
        fis = null

        //send '\0' to end it
        buf[0] = 0
        out.write(buf, 0, 1)
        out.flush()
        if (showProgress) {
            System.out.print "]"
            System.out.println()
        }

        if (checkAck(inStream) != 0) {
            out.close()
            channel.disconnect()
            throw new FileUploadException(sourceFilename);
        }

        logger.lifecycle "Successfully uploaded"

        out.close()
        channel.disconnect()
    }

    private void createRemoteDir(Session session, String path) {
        String command = "mkdir '" + path + "'"
        Channel channel = session.openChannel("exec")

        ((ChannelExec) channel).setCommand(command)

        OutputStream out = channel.getOutputStream()
        InputStream inStream = channel.getInputStream()

        channel.connect()

        logger.lifecycle "Creating directory: '${path}'"
        logger.debug "Executing $command"

        channel.close()
        out = null
        inStream = null
    }

    private boolean deleteDir(File path) throws FileDeleteException {
        if (path.exists()) {
            if (path.isDirectory()) {
                if (!path.deleteDir()) {
                    new FileDeleteException(path)
                }
                logger.debug "Deleted directory '${path.path}'"
            } else {
                if (!path.delete()) {
                    new FileDeleteException(path)
                }
                logger.debug "Deleted '${path.path}'"
            }
        }
        return true
    }

    public static int checkAck(InputStream stream) throws IOException {
        int b = stream.read()
        // b may be 0 for success,
        // 1 for error,
        // 2 for fatal error,
        // -1
        if (b == 0) return b
        if (b == -1) return b

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer()
            sb.append("           ")
            int c
            while (c != '\n' && c != -1) {
                c = stream.read()
                sb.append((char) c)
            }
            if (b == 1) { // error
                print sb.toString()
            }
            if (b == 2) { // fatal error
                print sb.toString()
            }
        }
        return b
    }
}