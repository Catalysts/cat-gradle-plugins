package cc.catalysts.gradle.plugins.i18n

import static cc.catalysts.gradle.plugins.i18n.I18nPropertyLine.parseLine


class I18nTemplate {
    String templateFileName
    long lines;
    long properties
    long comments
    long emptyLines
    List<I18nPropertyLine> content
    Set<String> propertySet;
    Set<String> missingProperties = []

    private I18nTemplate(String templateFile) {
        templateFileName = templateFile
        lines = 0
        properties = 0
        emptyLines = 0
        comments = 0
        content = []
        propertySet = []
    }

    static I18nTemplate create(File f) {
        if (f == null) {
            throw new FileNotFoundException(f)
        }
        I18nTemplate t = new I18nTemplate(f.getName())
        BufferedReader reader = new BufferedReader(new FileReader(f))
        String curLine = reader.readLine()
        while (curLine != null) {
            t.addLine(curLine)
            curLine = reader.readLine()
        }
        return t
    }

    void addLine(String line) {
        lines++
        I18nPropertyLine l = parseLine(line, lines)
        switch (l.getType()) {
            case I18nLineType.PROPERTY:
                properties++
                propertySet.add(l.getProperty().getName())

                break;
            case I18nLineType.EMPTY_LINE:
                emptyLines++
                break;
            case I18nLineType.COMMENT:
                comments++
                break;
        }
        content.add(l)
    }

    boolean compareWith(File file, I18nExtension e) {
        I18nTemplate t = create(file)
        println('Comparing "' + templateFileName + '" with "' + t.getTemplateFileName() + '"')
        Set<String> missingPropertiesTemplate = []
        missingPropertiesTemplate.addAll(propertySet)
        boolean orderError = false;
        int firstOccurrence = 0;
        I18nPropertyLine curLineTemplate;
        I18nPropertyLine curLine
        for (int i = 0; i < t.getContent().size(); i++) {
            if (i < content.size()) {
                curLineTemplate = content.get(i)
            }
            curLine = t.getContent().get(i)

            if (curLine.type == I18nLineType.PROPERTY) {
                if (!propertySet.contains(curLine.getProperty().getName())) {
                    missingProperties.add(curLine.getProperty().getName())
                }
                missingPropertiesTemplate.remove(curLine.getProperty().getName())
            }

            if (e.checkOrder && curLine.compareTo(curLineTemplate) != 0) {
                orderError = true
                firstOccurrence = i + 1;
            }
        }
        if (orderError) {
            e.getErrors().add('Properties in file "' + t.getTemplateFileName() + '" and "' + templateFileName + '" are not in the same order (first occurrence: ' + firstOccurrence + ')')
        }
        if (missingPropertiesTemplate.size() != 0) {
            e.getErrors().add('File "' + t.getTemplateFileName() + '" is missing the following properties: ')
            for (String m : missingPropertiesTemplate) {
                e.getErrors().add('\t\t' + m)
            }
        }
    }
}
