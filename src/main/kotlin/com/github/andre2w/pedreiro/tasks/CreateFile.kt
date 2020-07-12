package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment

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