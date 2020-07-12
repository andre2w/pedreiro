package com.github.andre2w.environment

import com.github.andre2w.tasks.CommandParser
import java.io.File
import javax.inject.Singleton

@Singleton
class ProcessExecutor(
        private val commandParser : CommandParser
) {
    fun execute(command: String, runFolder: String) : Int {
        val process = ProcessBuilder()
                .command(commandParser.parseCommand(command))
                .directory(File(runFolder))
                .start()

        process.waitFor()

        return process.exitValue()
    }
}
