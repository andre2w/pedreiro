package com.github.andre2w

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.Pedreiro
import com.github.andre2w.pedreiro.blueprints.BlueprintParsingException
import com.github.andre2w.pedreiro.configuration.ConfigurationNotFound
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class PedreiroCommandShould {

    private val pedreiro = mockk<Pedreiro>(relaxUnitFun = true)
    private val consoleHandler = mockk<ConsoleHandler>(relaxed = true)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `run Pedreiro application with arguments`() {
        val ctx = newPedreiroContext()

        val args = arrayOf("testBlueprint", "--arg", "test=blueprint", "--arg", "other=field", "--arg", "other=field")
        PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)

        verify {
            pedreiro.build(parsedArguments())
        }
        ctx.close()
    }

    @Test
    internal fun `print message and exit with code 1 in case of an error`() {
        val ctx = newPedreiroContext()
        val args = arrayOf("testBlueprint", "--arg", "test=blueprint", "--arg", "other=field", "--arg", "other=field")
        val parsedArguments = parsedArguments()
        val errorMessage = "Failed to parse blueprint"
        every { pedreiro.build(parsedArguments) } throws BlueprintParsingException(errorMessage)

        PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)

        verify {
            consoleHandler.exitWith(1)
            consoleHandler.print(errorMessage)
        }
        ctx.close()
    }

    @Test
    internal fun `print message with configuration path and exit with code 2 when there is a configuration error`() {
        val ctx = newPedreiroContext()
        val args = arrayOf("testBlueprint", "--arg", "test=blueprint", "--arg", "other=field", "--arg", "other=field")
        val parsedArguments = parsedArguments()
        val configFilePath = "/home/users/andre/.pedreiro/configuration.yml"
        every { pedreiro.build(parsedArguments) } throws ConfigurationNotFound(configFilePath)

        PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)

        verify {
            consoleHandler.exitWith(2)
            consoleHandler.print("Failed to load configuration: $configFilePath")
        }
        ctx.close()
    }

    private fun parsedArguments(): Arguments {
        return Arguments("testBlueprint", mapOf(
                "test" to "blueprint",
                "other" to "field"
        ))
    }

    private fun newPedreiroContext(): ApplicationContext {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        ctx.registerSingleton(pedreiro)
        ctx.registerSingleton(consoleHandler)
        return ctx
    }


}
