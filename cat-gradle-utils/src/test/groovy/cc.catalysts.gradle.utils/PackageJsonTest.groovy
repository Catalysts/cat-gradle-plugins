package cc.catalysts.gradle.utils

import cc.catalysts.gradle.npm.PackageJson
import org.junit.Test

import static org.junit.Assert.assertEquals

class PackageJsonTest {
    @Test
    public void testEmpty() {
        assertEquals('{}', new PackageJson().build())
    }

    @Test
    public void testPrivate() {
        assertEquals("""{
  "private": true
}""", PackageJson.initPrivate().build())
    }

    @Test
    public void testPrivateWithDependency() {
        assertEquals("""{
  "private": true,
  "dependencies": {
    "dependency1": "1.0.3"
  }
}""", PackageJson
                .initPrivate()
                .addDependency('dependency1', '1.0.3')
                .build())
    }

    @Test
    public void testPrivateWithDependencies() {
        assertEquals("""{
  "private": true,
  "dependencies": {
    "dependency1": "1.0.3",
    "dependency2": "~2.5.3",
    "dependency3": "[3.0.8,)"
  }
}""", PackageJson
                .initPrivate()
                .addDependency('dependency1', '1.0.3')
                .addDependency('dependency2', '~2.5.3')
                .addDependency('dependency3', '[3.0.8,)')
                .build())
    }
}