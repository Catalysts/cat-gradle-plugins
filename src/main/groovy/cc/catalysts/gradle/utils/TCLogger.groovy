package cc.catalysts.gradle.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

/**
 * Teamcity logger wrapper<br/>
 * If an teamcity build is detected it will format all logging messages as teamcity build messages
 * @author Catalysts GmbH, www.catalysts.cc
 */
class TCLogger {
    private Project project
    private Logger logger
    public final boolean isTeamCityBuild

    TCLogger(Project project, Logger logger) {
        this.project = project
        this.logger = logger
        isTeamCityBuild = isTeamCityBuild(project)
    }

    static boolean isTeamCityBuild(Project project) {
        return project?.hasProperty("teamcity") || project?.parent?.hasProperty("teamcity")
    }

    void error(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[message text='${escapeString(message)}' status='ERROR']"
        } else {
            logger.error(message)
        }
    }

    void info(String message) {
        if (isTeamCityBuild) {
            if (logger.isInfoEnabled()) {
                println "##teamcity[message text='${escapeString(message)}']"
            }
        } else {
            logger.info(message)
        }
    }

    void lifecycle(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[message text='${escapeString(message)}']"
        } else {
            logger.lifecycle(message)
        }
    }

    void warn(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[message text='${escapeString(message)}' status='WARNING']"
        } else {
            logger.warn(message)
        }
    }

    void debug(String message) {
        logger.debug(message)
    }

    /**
     * formats as teamcity build stopping message if teamcity build is detected
     */
    void failure(String message, boolean stopBuild = false) {
        if (isTeamCityBuild) {
            println "##teamcity[buildProblem description='${escapeString(message)}']"
            if (stopBuild) {
                throw new RuntimeException(message)
            }
        } else {
            logger.error(message)
            if (stopBuild) {
                throw new RuntimeException(message)
            }
        }
    }

    void openBlock(String block) {
        if (isTeamCityBuild) {
            println "##teamcity[blockOpened name='${escapeString(block)}']"
        }
    }

    void closeBlock(String block) {
        if (isTeamCityBuild) {
            println "##teamcity[blockClosed name='${escapeString(block)}']"
        }
    }

    void progressStart(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[progressStart '${escapeString(message)}']"
        }
    }

    void progressFinish(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[progressFinish '${escapeString(message)}']"
        }
    }

    void progressMessage(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[progressMessage '${escapeString(message)}']"
        }
    }

    /**
     * escapes string to TeamCity 8 specification for build messages
     * @param string the string to be escaped
     * @return the escaped string
     */
    static escapeString(String string) {
        return string?.replace("|", "||")?.replace("'", "|'")?.replace("\n", "|n")?.replace("\r", "|r")?.replace("[", "|[")?.replace("]", "|]")
    }
}
