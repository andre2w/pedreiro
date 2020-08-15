package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class ExecuteCommandShould {

    private val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)
    private val runFolder = "/home/andre/projects"
    private val command = "gradle wrapper"

    @Test
    fun `execute command in the proper folder`() {
        val environment = mockk<LocalEnvironment>()
        every { processExecutor.execute(command, "$runFolder/test-project") } returns 0
        every { environment.currentDir() } returns runFolder

        val executeCommand = ExecuteCommand(
                "gradle wrapper",
                "test-project",
                processExecutor,
                environment
        )
        executeCommand.execute()

        verify {
            processExecutor.execute(command, "$runFolder/test-project")
        }
    }
}
