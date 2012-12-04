package cc.catalysts.gradle.plugins

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class FileDeleteException extends Exception {
    FileDeleteException(String message) {
        super(message);
    }
    FileDeleteException(File f){
        super("File \"" + f.getPath() + "\" could not be deleted");
    }
}