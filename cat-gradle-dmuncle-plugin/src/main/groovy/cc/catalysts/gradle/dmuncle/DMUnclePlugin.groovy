package cc.catalysts.gradle.dmuncle

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import net.sf.json.JSONObject
import org.apache.commons.lang.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.json.simple.JSONArray

/**
 * @author Cristian Ilca, Catalysts Romania on 18.0.2015.
 */

class DMUnclePluginExtension {
    String server
    int requestTimeout
}

class DMUnclePlugin implements Plugin<Project> {

    def allDeps = "";
    JSONObject JSONPackage = new JSONObject();
    JSONArray compileDeps = new JSONArray();
    JSONArray testCompileDeps = new JSONArray();
    JSONArray runtimeDeps = new JSONArray();
    JSONArray testRuntimeDeps = new JSONArray();

    void apply(Project project) {
        project.extensions.create("dmuncle", DMUnclePluginExtension)
        project.task('dmUncleWatch') << {
            println "Starting dm-uncle plugin"
            JSONPackage.put("projectName", project.getName());
            project.getAllprojects().each { proj ->
                proj.configurations.each { conf ->
                    if (conf.name == "compile") {
                        conf.allDependencies.each { dep ->
                            if (!dep.hasProperty('dependencyProject')) {
                                if (!allDeps.contains("${dep.group}:${dep.name}:${dep.version}")) {
                                    if(StringUtils.isNotEmpty(dep.group) && dep.group != "unspecified" && StringUtils.isNotEmpty(dep.name) && dep.name != "unspecified") {
                                        compileDeps.add(getDepsInJSONObject(dep.group, dep.name, dep.version));
                                    }
                                }
                                allDeps += "${dep.group}:${dep.name}:${dep.version}\n"
                            }
                        }
                    }
                    if (conf.name == "testCompile") {
                        conf.allDependencies.each { dep ->
                            if (!dep.hasProperty('dependencyProject')) {
                                if (!allDeps.contains("${dep.group}:${dep.name}:${dep.version}")) {
                                    if(StringUtils.isNotEmpty(dep.group) && dep.group != "unspecified" && StringUtils.isNotEmpty(dep.name) && dep.name != "unspecified") {
                                        testCompileDeps.add(getDepsInJSONObject(dep.group, dep.name, dep.version));
                                    }
                                }
                                allDeps += "${dep.group}:${dep.name}:${dep.version}\n"
                            }
                        }
                    }
                    if (conf.name == "runtime") {
                        conf.allDependencies.each { dep ->
                            if (!dep.hasProperty('dependencyProject')) {
                                if (!allDeps.contains("${dep.group}:${dep.name}:${dep.version}")) {
                                    if(StringUtils.isNotEmpty(dep.group) && dep.group != "unspecified" && StringUtils.isNotEmpty(dep.name) && dep.name != "unspecified") {
                                        runtimeDeps.add(getDepsInJSONObject(dep.group, dep.name, dep.version));
                                    }
                                }
                                allDeps += "${dep.group}:${dep.name}:${dep.version}\n"
                            }
                        }
                    }
                    if (conf.name == "testRuntime") {
                        conf.allDependencies.each { dep ->
                            if (!dep.hasProperty('dependencyProject')) {
                                if (!allDeps.contains("${dep.group}:${dep.name}:${dep.version}")) {
                                    if(StringUtils.isNotEmpty(dep.group) && dep.group != "unspecified" && StringUtils.isNotEmpty(dep.name) && dep.name != "unspecified") {
                                        testRuntimeDeps.add(getDepsInJSONObject(dep.group, dep.name, dep.version));
                                    }
                                }
                                allDeps += "${dep.group}:${dep.name}:${dep.version}\n"
                            }
                        }
                    }
                }
            }
            composeJSONPackage()
            sendDependencies(project.dmuncle.server, project.dmuncle.requestTimeout)
        }
    }

    JSONObject getDepsInJSONObject(String group, String name, String version) {
        JSONObject jsonDeps = new JSONObject();
        jsonDeps.put("groupId", group);
        jsonDeps.put("artifactId", name);
        jsonDeps.put("version", version);
        return jsonDeps;
    }

    void composeJSONPackage() {
        JSONPackage.put("compileArtifacts", compileDeps);
        JSONPackage.put("testCompileArtifacts", testCompileDeps);
        JSONPackage.put("runtimeArtifacts", runtimeDeps);
        JSONPackage.put("testRuntimeArtifacts", testRuntimeDeps);
    }

    void sendDependencies(String url, int timeout) {
        if(timeout == null) {
            timeout = 100000;
        }

        println "Start http request to " + url

        def http = new HTTPBuilder(url);
        http.ignoreSSLIssues()
        http.getClient().getParams().setParameter("http.connection.timeout", new Integer(timeout))
        http.getClient().getParams().setParameter("http.socket.timeout", new Integer(timeout))

        http.handler.success = { println "Successfuly sent data. Please check your project status at ${url}" }

        http.handler.failure = { resp ->
            println "Data was not sent. The JSON package has been saved locally."
            def destJSON = new File("dm-uncle-package.json")
            destJSON.text = JSONPackage
        }

        println "Sending 'POST' request to URL : " + url
        http.request(Method.POST, ContentType.JSON) {
            uri.path = '/import'
            body = JSONPackage
        };
    }
}