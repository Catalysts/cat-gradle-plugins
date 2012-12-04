package cc.catalysts.gradle.plugins

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class FileUploadException extends Exception {
    FileUploadException(String f) {
        super("File \"" + f + "\" could not be uploaded");
    }

    FileUploadException(File f){
        super("File \"" + f.getPath() + "\" could not be uploaded");
    }
}