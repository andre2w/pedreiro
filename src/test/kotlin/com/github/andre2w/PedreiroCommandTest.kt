package com.github.andre2w

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PedreiroCommandTest {

    @Test
    fun testWithCommandLineOption() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val pedreiro = mockk<Pedreiro>(relaxUnitFun = true)

        ctx.registerSingleton(pedreiro)
        val args = arrayOf("testBlueprint", "--arg", "test=blueprint", "--arg", "other=field", "--arg", "other=field")
        PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)

        ctx.close()

        verify {
            pedreiro.build(Arguments("testBlueprint", mapOf(
                    "test" to "blueprint",
                    "other" to "field"
            )))
        }
    }


}
