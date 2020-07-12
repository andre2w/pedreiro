package com.github.andre2w.blueprints

data class Blueprint(val tasks: String, val files: Map<String, String> = emptyMap()) {

    fun fileContentOf(filename: String): String {
        return files[filename]!!
    }
}
