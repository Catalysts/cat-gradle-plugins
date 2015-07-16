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

    private String indent
    private String increaseString

    TCLogger(Project project, Logger logger) {
        this.project = project
        this.logger = logger
        isTeamCityBuild = isTeamCityBuild(project)
        setIncrease(5)
    }

    void setIndent(String indent) {
        this.indent = indent
    }

    void increaseIndent() {
        setIndent(indent + increaseString)
    }

    void decreaseIndent() {
        if (indent.length() > increaseString.length()) {
            indent = indent.substring(increaseString.length())
        }
    }

    void setIncrease(int increase) {
        resetIndent()
        StringBuilder b = new StringBuilder()
        for (int i = 0; i < increase; i++) {
            b.append(' ')
        }
        increaseString = b.toString()
    }

    void resetIndent() {
        indent = ''
    }

    boolean isInfoEnabled() {
        return logger?.isInfoEnabled()
    }

    boolean isDebugEnabled() {
        return logger?.isDebugEnabled()
    }

    static boolean isTeamCityBuild(Project project) {
        return project?.hasProperty("teamcity") || project?.parent?.hasProperty("teamcity")
    }

    void error(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[message text='${indent + escapeString(message)}' status='ERROR']"
        } else {
            logger.error(indent + message)
        }
    }

    void info(String message) {
        if (isTeamCityBuild) {
            if (logger.isInfoEnabled()) {
                println "##teamcity[message text='${indent + escapeString(message)}']"
            }
        } else {
            logger.info(indent + message)
        }
    }

    void lifecycle(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[message text='${indent + escapeString(message)}']"
        } else {
            logger.lifecycle(indent + message)
        }
    }

    void warn(String message) {
        if (isTeamCityBuild) {
            println "##teamcity[message text='${indent + escapeString(message)}' status='WARNING']"
        } else {
            logger.warn(indent + message)
        }
    }

    void debug(String message) {
        logger.debug(indent + message)
    }

    /**
     * formats as teamcity build stopping message if teamcity build is detected
     */
    void failure(String message, boolean stopBuild = false) {
        if (isTeamCityBuild) {
            println "##teamcity[buildProblem description='${indent + escapeString(message)}']"
            if (stopBuild) {
                throw new RuntimeException(message)
            }
        } else {
            logger.error(indent + message)
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

    void buildNumber(String version) {
        if (isTeamCityBuild) {
            println "##teamcity[buildNumber '${version}']"
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
