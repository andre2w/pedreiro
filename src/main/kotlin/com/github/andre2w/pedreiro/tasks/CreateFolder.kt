package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment

data class CreateFolder(
        val path: String,
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment
) : Task {
    override fun execute() {
        fileSystemHandler.createFolder("${environment.currentDir()}/$path")
    }
}