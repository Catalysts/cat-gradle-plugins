package cc.catalysts.gradle.plugins.i18n

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class I18nPropertyLine {
    final long line
    final I18nLineType type
    final I18nProperty property

    private I18nPropertyLine(String line, long lineNr) {
        this.line = lineNr
        if (line == null || line.isEmpty()) {
            type = I18nLineType.EMPTY_LINE
            property = null
        } else if (line.startsWith('#')) {
            type = I18nLineType.COMMENT
            property = null
        } else {
            type = I18nLineType.PROPERTY
            property = I18nProperty.createFromLine(line)
        }
    }

    static I18nPropertyLine parseLine(String line, long lineNr) {
        if (line == null) {
            return null
        }
        return new I18nPropertyLine(line.trim(), lineNr)
    }

    public int compareTo(I18nPropertyLine o) {
        if(o == null){
            return -1
        }
        if (type != o.getType()) {
            return 2
        }
        if (type == I18nLineType.PROPERTY) {
            return property.getName().compareTo(o.getProperty().getName())
        }
        return 0
    }

    @Override
    boolean equals(Object obj) {
        if (!obj instanceof I18nPropertyLine) {
            return false;
        }
        return compareTo(obj as I18nPropertyLine) == 0
    }
}
