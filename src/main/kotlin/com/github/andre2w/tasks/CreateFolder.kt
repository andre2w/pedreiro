package com.github.andre2w.tasks

import com.github.andre2w.environment.FileSystemHandler
import com.github.andre2w.environment.LocalEnvironment

data class CreateFolder(
        val path: String,
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment
) : Task {
    override fun execute() {
        fileSystemHandler.createFolder("${environment.currentDir()}/$path")
    }
}