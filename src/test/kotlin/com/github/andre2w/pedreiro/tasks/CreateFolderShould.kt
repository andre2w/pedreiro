package com.github.andre2w.pedreiro.tasks

import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class CreateFolderShould {

    @Test
    fun `create folder in the specified path`() {
        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<LocalEnvironment>()
        every { environment.currentDir() } returns "/home/andre/projects"

        val createFolder = CreateFolder(
                "test-folder",
                fileSystemHandler,
                environment
        )
        createFolder.execute()

        verify {
            fileSystemHandler.createFolder("/home/andre/projects/test-folder")
        }
    }

}