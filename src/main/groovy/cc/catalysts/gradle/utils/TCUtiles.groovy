package cc.catalysts.gradle.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger


class TCUtiles {
    static isTeamCityBuild(Project p){
        return p.hasProperty("teamcity") || p.parent.hasProperty("teamcity")
    }

    /**
     * logs to gradle logger or formats as teamcity error message if teamcity build is detected
     * @param logger the logger to log the error
     */
    static error(Project p, Logger logger, String message){
        if(isTeamCityBuild(p)){
            println "##teamcity[message text='${message}' status='ERROR']"
        }else{
            logger.error(message)
        }
    }

    /**
     * logs to gradle logger or formats as teamcity warning message if teamcity build is detected
     * @param logger the logger to log the error
     */
    static warn(Project p, Logger logger, String message){
        if(isTeamCityBuild(p)){
            println "##teamcity[message text='${message}' status='WARN']"
        }else{
            logger.warn(message)
        }
    }

    /**
     * formats as teamcity failure message if teamcity build is detected
     */
    static failure(Project p, String message){
        if(isTeamCityBuild(p)){
            println "##teamcity[message text='${message}' status='FAILURE']"
        }
    }
}
