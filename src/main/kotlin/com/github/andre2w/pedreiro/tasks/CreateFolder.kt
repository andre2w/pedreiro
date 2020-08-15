package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment

data class CreateFolder(
        val path: String,
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment,
        private val consoleHandler: ConsoleHandler
) : Task {
    override fun execute() {
        val folderPath = "${environment.currentDir()}/$path"
        consoleHandler.printDebug("Creating folder $folderPath")
        fileSystemHandler.createFolder(folderPath)
    }
}
