package com.github.andre2w.fixtures

class FixtureLoader(private val folder: String, private val defaultExtension: String?) {

    init {
        validateExtension()
    }

    operator fun invoke(fileName: String): String {
        return loadFile(fileName.normalised())
    }

    private fun loadFile(fileToLoad: String): String {
        return FixtureLoader::class.java.getResource(fileToLoad).readText()
    }

    private fun String.normalised(): String {
        return if (hasExtensionInName(this)) {
            "/Fixtures/$folder/$this"
        } else {
            "/Fixtures/$folder/$this$defaultExtension"
        }
    }

    private fun hasExtensionInName(fileName: String) = fileName.matches(Regex(".+\\.\\w+$"))

    private fun validateExtension() {
        defaultExtension?.let { extension ->
            if (!extension.startsWith("."))
                throw IllegalArgumentException("File extension must start with dot")
        }
    }
}
