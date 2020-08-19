package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment

data class CreateFile(
    private val path: String,
    private val content: String,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: LocalEnvironment,
    private val consoleHandler: ConsoleHandler
) : Task {
    override fun execute() {
        val filePath = "${environment.currentDir()}/$path"
        consoleHandler.printDebug("Creating file $filePath")
        fileSystemHandler.createFile(filePath, content)
    }
}
