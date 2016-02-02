package cc.catalysts.gradle.npm

import java.nio.file.Files

/**
 * @author Thomas Scheinecker, Catalysts GmbH
 */
final class PackageJson {
    private String indent = '  ';
    private boolean privateModule = false;
    private Map<String, String> dependencies = [:];

    public PackageJson() {}

    private PackageJson(Closure config) {
        config(this)
    }

    private PackageJson(PackageJson packageJsonBuilder, Closure config = {}) {
        privateModule = packageJsonBuilder.privateModule
        dependencies.putAll(packageJsonBuilder.dependencies)
        config(this)
    }

    static PackageJson initPrivate() {
        return new PackageJson({ it.privateModule = true })
    }

    PackageJson addDependency(String name, String version) {
        new PackageJson(this, { it.dependencies.put(name, version) })
    }


    PackageJson addDependencies(Map<String, String> dependencies) {
        new PackageJson(this, { it.dependencies.putAll(dependencies) })
    }

    File toFile(File file, boolean mkdirs = true) {
        if (mkdirs && !file.parentFile.exists()) {
            Files.createDirectories(file.parentFile.toPath())
        }

        file.text = toString()
        return file;
    }

    boolean equals(File file) {
        if (!file.exists()) {
            return false
        }

        String fileContent = file.readLines().join('\n')
        return fileContent.equals(build())
    }

    String build() {
        return toString()
    }

    private String getPrivateConfig() {
        return privateModule ? /${indent}"private": true/ : ''
    }

    private String getDependenciesConfig() {
        return dependencies.isEmpty() ? '' : """${indent}"dependencies": {
${dependencies.collect { /${indent}${indent}"${it.key}": "${it.value}"/ }.join(',\n')}
${indent}}"""
    }

    @Override
    String toString() {
        List<String> configs = []
        configs.add(getPrivateConfig())
        configs.add(getDependenciesConfig())
        StringBuilder packageJson = new StringBuilder('{\n')

        List<String>  cleanConfigs = configs.findAll({ !it.empty })

        if (cleanConfigs.empty) {
            return '{}'
        }

        packageJson.append(cleanConfigs.join(',\n'))
        packageJson.append('\n}');
        return packageJson.toString()
    }
}
