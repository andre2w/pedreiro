package com.github.andre2w.tasks

import com.github.andre2w.environment.LocalEnvironment
import com.github.andre2w.environment.ProcessExecutor

data class ExecuteCommand(
        val command: String,
        val folder: String,
        private val processExecutor: ProcessExecutor,
        private val environment: LocalEnvironment
) : Task {
    override fun execute() {
        processExecutor.execute(command, "${environment.currentDir()}/$folder")
    }
}