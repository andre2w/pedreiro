package com.github.andre2w.tasks

import com.github.andre2w.environment.FileSystemHandler
import com.github.andre2w.environment.LocalEnvironment

data class CreateFile(
        val path: String,
        val content: String,
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment
) : Task {
    override fun execute() {
        fileSystemHandler.createFile("${environment.currentDir()}/$path",content)
    }
}