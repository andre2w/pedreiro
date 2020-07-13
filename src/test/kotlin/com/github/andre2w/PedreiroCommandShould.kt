package com.github.andre2w

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.Pedreiro
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test


class PedreiroCommandShould {

    @Test
    fun testWithCommandLineOption() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val pedreiro = mockk<Pedreiro>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxed = true)
        ctx.registerSingleton(pedreiro)
        ctx.registerSingleton(consoleHandler)

        val args = arrayOf("testBlueprint", "--arg", "test=blueprint", "--arg", "other=field", "--arg", "other=field")
        PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)

        verify {
            pedreiro.build(Arguments("testBlueprint", mapOf(
                    "test" to "blueprint",
                    "other" to "field"
            )))
        }
        ctx.close()
    }


}
