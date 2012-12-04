package cc.catalysts.gradle.plugins.deploy

import cc.catalysts.gradle.plugins.FileDeleteException
import cc.catalysts.gradle.plugins.FileUploadException
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
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

                File logDir = new File(usedConfig.webappDir + File.separator +"logs")
                File webappDir = new File(usedConfig.webappDir + File.separator +"webapps")
                File pluginDir = new File(usedConfig.webappDir + File.separator +"plugins")
                File workDir = new File(usedConfig.webappDir + File.separator +"work")

                println "Deploying " + usedConfig.webappWar + " to " + usedConfig.tomcatHost

                println "-- Stopping Tomcat Service.."
                RunCommand(true, ["sc", usedConfig.tomcatHost, "stop", usedConfig.tomcatService])

                if (deleteDir(logDir)){
                    println "   Successfully deleted " + logDir.getPath()
                }
                if (deleteDir(webappDir)){
                    println "   Successfully deleted " + webappDir.getPath()
                }
                if (deleteDir(pluginDir)){
                    println "   Successfully deleted " + pluginDir.getPath()
                }
                if (deleteDir(workDir)){
                    println "   Successfully deleted " + workDir.getPath()
                }

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
                def webappDir = usedConfig.webappDir

                println 'Uploading ' + webappDir + ' to ' + usedConfig.uploadTarget

                sendFiles(usedConfig, webappDir)

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

    private void sendFiles(usedConfig, webappDir) {

        Session session = null

        println '  -- Connecting to ' + usedConfig.uploadTarget
        JSch jsch = new JSch()
        println '    -- getting Session'
        session = jsch.getSession(usedConfig.username, usedConfig.uploadTarget, 22)
        session.setConfig("StrictHostKeyChecking", "no")
        println '    -- set password'
        session.setPassword(usedConfig.password)
        println '    -- connect'
        session.connect()
        session.sendKeepAliveMsg()

        
        String baseUploadDir = usedConfig.uploadDir + '/' + project.version

        File sendDir = new File(webappDir)
        File[] dirList = sendDir.listFiles()
        if ( dirList.size() == 0){
            '    -- webApp directory is empty - no files to upload'
        }else{
            createRemoteDir(session, baseUploadDir)
            
            uploadDir(session, usedConfig, dirList, baseUploadDir)
        }
        println '    -- Finished'
        session.disconnect()
        session = null
    }

    private static void uploadDir(Session session, def usedConfig,File[] dirList, String directory) {
        if (dirList == null || session == null || directory == null || usedConfig == null){
            return;
        }
        for(int i=0; i < dirList.length; i++) {
            File f = dirList[i]
            if (f.isDirectory()){
                String newDir = directory + '/' +f.getName()
                createRemoteDir(session, newDir)
                uploadDir(session, usedConfig, f.listFiles(), newDir)
            }else{
                uploadSshFile(session, f, f.getPath(), directory + '/' + f.getName())
            }
        }
    }

    private static void uploadSshFile(Session session, File f, String sourceFilename, String destFilename) {
        String command = null
        OutputStream out = null
        InputStream inStream = null
        Channel channel = null
        
        long fileSize = f.length()

        println '    -- Sending ' + sourceFilename + ' to ' + destFilename + ' (~' + (long)(fileSize / 1024) + ' KB)'

        FileInputStream fis = null

        // SCP UPLOAD
        command = "scp -p -t '" + destFilename + "'"
        channel = session.openChannel("exec")
        ((ChannelExec) channel).setCommand(command)

        out = channel.getOutputStream()
        inStream = channel.getInputStream()

        channel.connect()

        println "         Command: " + command

        if(checkAck(inStream) != 0 ){
            throw new FileUploadException(sourceFilename);
        }else{
            println "           Acknowledge successful"
        }

        // create File and upload
        command = "C0644 " + fileSize + " "
        if (sourceFilename.lastIndexOf('/') > 0) {
            command += sourceFilename.substring(sourceFilename.lastIndexOf('/') + 1)
        } else {
            command += sourceFilename
        }
        command += "\n"

        print "         Command: " + command

        out.write(command.getBytes())
        out.flush()

        if(checkAck(inStream) != 0 ){
            throw new FileUploadException(sourceFilename);
        }else{
            println "           Acknowledge successful"
        }

        fis = new FileInputStream(sourceFilename)
        boolean showProgress = false
        byte[] buf = new byte[1024]
        int sendBuffer = 0
        long fivePercent = fileSize / 20
        int completed = 0
        if (fileSize > 1024 * 50){
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
            if(showProgress && sendBuffer > fivePercent){
                // 5% sended
                sendBuffer = sendBuffer - fivePercent
                completed += 5
                if(completed == 25){
                    System.out.print '25%'
                } else if(completed == 50){
                    System.out.print '50%'
                } else if(completed == 75){
                    System.out.print '75%'
                } else if(completed == 100){
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
        if (showProgress){
            System.out.print "]"
            System.out.println()
        }
        println '       Successfully uploaded'

        if(checkAck(inStream) != 0 ){
            throw new FileUploadException(sourceFilename);
        }

        out.close()

        channel.disconnect()
    }

    private static void createRemoteDir(Session session, String path) {
        String command = "mkdir '" + path + "'"
        Channel channel = session.openChannel("exec")

        ((ChannelExec) channel).setCommand(command)

        OutputStream out = channel.getOutputStream()
        InputStream inStream = channel.getInputStream()

        channel.connect()

        println "    -- Creating directory: '" + path + "'"
        println "         Command: " + command
        
        channel.close()
        out = null
        inStream = null
    }

    private static boolean deleteDir(File path) throws FileDeleteException {
        if( path.exists() ) {
            if (path.isDirectory()){
                if (!path.deleteDir()){
                    new FileDeleteException(path)
                }
            }else{
                if (!path.delete()){
                    new FileDeleteException(path)
                }
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
        if(b == 0) return b
        if(b == -1) return b

        if(b == 1 || b == 2){
            StringBuffer sb = new StringBuffer()
            sb.append("           ")
            int c
            while(c != '\n' && c != -1){
                c = stream.read()
                sb.append((char)c)
            }
            if(b == 1 ){ // error
                print sb.toString()
            }
            if(b == 2 ){ // fatal error
                print sb.toString()
            }
        }
        return b
    }
    
    private static disconnect(Session session, Channel channel){
        if (channel != null){
            channel.disconnect()
        }
        if(session != null){
            session.disconnect()
        }
    }
}