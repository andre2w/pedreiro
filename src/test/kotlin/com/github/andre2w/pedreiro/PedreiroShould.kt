package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.blueprints.BlueprintService
import com.github.andre2w.pedreiro.tasks.CreateFolder
import com.github.andre2w.pedreiro.tasks.Tasks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PedreiroShould {

    private val createFolder = mockk<CreateFolder>(relaxUnitFun = true)
    private val createChildFolder = mockk<CreateFolder>(relaxUnitFun = true)

    @Test
    internal fun `load blueprint and execute tasks`() {
        val blueprintService = mockk<BlueprintService>()
        val arguments = Arguments("test-blueprint")
        val tasks = Tasks.of(listOf(
                createFolder,
                createChildFolder
        ))
        every { blueprintService.loadBlueprint(arguments) } returns tasks

        val pedreiro = Pedreiro(blueprintService)
        pedreiro.build(arguments)

        verify {
            tasks.forEach { task -> task.execute() }
        }
    }
}
