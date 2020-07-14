package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.blueprints.BlueprintService
import com.github.andre2w.pedreiro.tasks.Tasks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PedreiroShould {

    @Test
    internal fun `load blueprint and execute tasks`() {
        val blueprintService = mockk<BlueprintService>()
        val scaffoldingService = mockk<ScaffoldingService>(relaxUnitFun = true)
        val arguments = Arguments("test-blueprint")
        val tasks = Tasks(emptyList())
        every { blueprintService.loadBlueprint(arguments) } returns tasks

        val pedreiro = Pedreiro(blueprintService, scaffoldingService)
        pedreiro.build(arguments)

        verify {
            scaffoldingService.execute(tasks)
        }
    }
}