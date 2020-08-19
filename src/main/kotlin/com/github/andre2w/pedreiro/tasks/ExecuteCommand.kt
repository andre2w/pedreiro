package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor

data class ExecuteCommand(
    private val command: String,
    private val folder: String,
    private val processExecutor: ProcessExecutor,
    private val environment: LocalEnvironment
) : Task {
    override fun execute() {
        processExecutor.execute(command, "${environment.currentDir()}/$folder")
    }
}
