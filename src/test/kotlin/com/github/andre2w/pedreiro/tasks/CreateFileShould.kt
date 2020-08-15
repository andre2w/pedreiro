package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CreateFileShould {

    @Test
    fun `create file with contents`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<LocalEnvironment>()
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val content =
                """
            plugins {
                id 'java'
            }
            """.trimIndent()
        every { environment.currentDir() } returns "/home/andre/project"

        val createFile = CreateFile(
                "test-project/build.gradle",
                content,
                fileSystemHandler,
                environment,
                consoleHandler
        )
        createFile.execute()

        verify {
            fileSystemHandler.createFile("/home/andre/project/test-project/build.gradle", content)
        }
    }
}
