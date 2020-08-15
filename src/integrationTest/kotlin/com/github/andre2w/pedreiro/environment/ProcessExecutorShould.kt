package com.github.andre2w.pedreiro.environment

import com.github.andre2w.pedreiro.tasks.CommandParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProcessExecutorShould {

    @Test
    fun `execute shell commands in the specified folder`() {
        val processExecutor = ProcessExecutor(CommandParser(), ConsoleHandler())
        val userDir = System.getProperty("user.dir")
        val fileName = "test-${rightNow()}.txt"

        val exitCode = processExecutor.execute("touch $fileName", userDir)

        val createdFile = "$userDir/$fileName"
        assertThat(Files.exists(Paths.get(createdFile))).isTrue()
        assertThat(exitCode).isEqualTo(0)
        Files.delete(Paths.get(createdFile))
    }

    private fun rightNow(): String {
        val format = DateTimeFormatter.ofPattern("YYYY-MM-dd-hhmmss")
        return LocalDateTime.now().format(format)
    }
}
