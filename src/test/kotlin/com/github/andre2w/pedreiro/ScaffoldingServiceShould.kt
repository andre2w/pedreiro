package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.tasks.CreateFile
import com.github.andre2w.pedreiro.tasks.CreateFolder
import com.github.andre2w.pedreiro.tasks.Tasks
import com.github.andre2w.pedreiro.tasks.from
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScaffoldingServiceShould {

    private val scaffoldingService = ScaffoldingService()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `execute all tasks`() {
        val createFolder = mockk<CreateFolder>(relaxUnitFun = true)
        val createFile = mockk<CreateFile>(relaxUnitFun = true)

        val blueprint = Tasks.from(
            createFolder,
            createFile
        )

        scaffoldingService.execute(blueprint)

        verifyOrder {
            createFolder.execute()
            createFile.execute()
        }
    }
}
