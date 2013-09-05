package cc.catalysts.gradle.plugins.i18n

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class I18nProperties {
    List<I18nPropertyFileGroup> groups

    public I18nProperties() {
        groups = []
    }

    I18nPropertyFileGroup hasGroup(String name) {
        for (I18nPropertyFileGroup g : groups) {
            if (name.equalsIgnoreCase(g.getName())) {
                return g;
            }
        }
        return null;
    }

    boolean addGroup(I18nPropertyFileGroup group) {
        if (group == null || group.getName() == null || group.getName().length() == 0 || group.getFiles() == null || group.getFiles().size() == 0) {
            return false;
        }
        return groups.add(group)
    }

    boolean isEmpty() {
        return groups.isEmpty()
    }
}
