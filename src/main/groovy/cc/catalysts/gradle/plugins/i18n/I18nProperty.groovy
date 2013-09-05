package cc.catalysts.gradle.plugins.i18n

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class I18nProperty {
    final String name
    final String value

    private I18nProperty(String line) {
        String[] data = line.trim().split("=")
        name = data[0]
        if (name == null) {
            throw new RuntimeException('Line "' + line + '" is not a valid property line')
        }
        if (data.size() == 2) {
            value = data[1]
        } else {
            value = null
        }
    }

    static I18nProperty createFromLine(String line) {
        return new I18nProperty(line)
    }
}
