package com.github.andre2w.fixtures

class FixtureLoader(private val folder: String) {

    operator fun invoke(fileName: String): String {
        return FixtureLoader::class.java.getResource("/Fixtures/$folder/$fileName").readText()
    }
}