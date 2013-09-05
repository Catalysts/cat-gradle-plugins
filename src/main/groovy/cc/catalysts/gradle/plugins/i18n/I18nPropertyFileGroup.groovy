package cc.catalysts.gradle.plugins.i18n

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class I18nPropertyFileGroup {
    String name;
    List<String> files;
    I18nTemplate template

    public I18nPropertyFileGroup(String file) {
        if (file == null || file.length() == 0) {
            throw new RuntimeException("Cannot build i18n property group from empty file name")
        }
        files = []
        name = getPropertyFileName(file)
        addFile(file)
    }

    boolean addFile(String file) {
        files.add(file);
    }

    void createMasterTemplate(File root) {
        template = I18nTemplate.create(new File(root, files.get(0)))
    }

    static String getPropertyFileName(String file) {
        if (file == null || file.length() == 0) {
            throw new RuntimeException("An empty String is not a valid i18n properties file name")
        }
        String name = file.replace(".properties", "")
        int underScore = name.indexOf('_')
        if (underScore != -1) {
            name = name.substring(0, underScore)
        }
        if (name == null || name.length() == 0) {
            throw new RuntimeException('"' + file + '" is not a valid i18n properties file')
        }
        return name
    }

    static boolean isPropertyFile(String file) {
        if (file == null || file.length() == 0) {
            throw new RuntimeException("An empty String is not a valid i18n properties file name")
        }
        return file.endsWith(".properties")
    }

    public boolean verify(File rootDir, I18nExtension extension) {
        for (String fName : files) {
            if (!fName.equals(template.getTemplateFileName())) {
                template.compareWith(new File(rootDir, fName), extension)
            }
        }

        if (template.getMissingProperties().size() != 0) {
            extension.getErrors().add('File "' + template.getTemplateFileName() + '" is missing the following properties: ')
            for (String m : template.getMissingProperties()) {
                extension.getErrors().add('\t\t' + m)
            }
        }
    }
}
