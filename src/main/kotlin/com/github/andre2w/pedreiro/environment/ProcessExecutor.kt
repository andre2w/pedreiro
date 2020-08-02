package com.github.andre2w.pedreiro.environment

import com.github.andre2w.pedreiro.tasks.CommandParser
import java.io.File
import java.io.InputStreamReader
import javax.inject.Singleton

@Singleton
class ProcessExecutor(
        private val commandParser: CommandParser,
        private val consoleHandler: ConsoleHandler
) {
    fun execute(command: String, runFolder: String) : Int {

        val parsedCommand = commandParser.parse(command)
        consoleHandler.printDebug("Executing command: \"$parsedCommand\"")

        val process = ProcessBuilder()
                .command(parsedCommand)
                .directory(File(runFolder))
                .start()

        InputStreamReader(process.inputStream).useLines { lines ->
            lines.forEach(consoleHandler::printDebug)
        }

        process.waitFor()

        return process.exitValue()
    }
}
