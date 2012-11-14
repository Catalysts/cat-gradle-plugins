package cc.catalysts.gradle.plugins

/**
 * @author Catalysts GmbH, www.catalysts.cc
 */
class TestUtils {

    static public void compareDirectories(File output, File expectation) {
        int outputFiles = 0
        output.eachFileRecurse { File file ->
            File expectationFile = new File(expectation, file.name)
            assert file.text.normalize() == expectationFile.text.normalize(), "File $file.path and $expectationFile.path differ"
            outputFiles++
        }

        int expectationFiles = 0
        expectation.eachFileRecurse {
            expectationFiles++
        }

        assert outputFiles == expectationFiles, "Number of files in output directory ($outputFiles files) and expectation directory ($expectationFiles files) differ"
    }

    static public File getTempDirectory() {
        File temp = File.createTempFile("catalysts-gradle-plugins", "")
        temp.delete()
        temp.mkdir()
        return temp
    }

}
