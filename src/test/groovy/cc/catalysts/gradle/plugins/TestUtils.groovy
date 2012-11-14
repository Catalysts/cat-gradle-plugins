package cc.catalysts.gradle.plugins

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class TestUtils {

    static public void compareDirectories(File output, File expectation) {
        List outputFiles = []
        output.eachFileRecurse { File file ->
            File expectationFile = new File(expectation, file.name)
            assert file.text.normalize() == expectationFile.text.normalize(), "File $file.path and $expectationFile.path differ"
            outputFiles.add(file)
        }

        List expectationFiles = []
        expectation.eachFileRecurse { File file ->
            expectationFiles.add(file)
        }

        assert outputFiles.size() == expectationFiles.size(), "Number of files in output directory $output and expectation directory $expectation differ!\n"+
                "Output files: $outputFiles\n"+
                "Expectation files: $expectationFiles"
    }

    static public File getTempDirectory() {
        File temp = File.createTempFile("catalysts-gradle-plugins", "")
        temp.delete()
        temp.mkdir()
        return temp
    }

}
