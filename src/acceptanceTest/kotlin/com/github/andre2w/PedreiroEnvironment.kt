package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.mockk.every
import io.mockk.mockk

class PedreiroEnvironment(
        private val fixtures: FixtureLoader
) {

    private val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
    val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
    val environment = mockk<LocalEnvironment>(relaxUnitFun = true)
    val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
    val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)
    val baseDir = "/home/user/projects"
    val homeDir = "/home/user/pedreiro"
    val configurationPath = "$homeDir/.pedreiro/configuration.yml"

    init {
        registerMocks()

        every { environment.currentDir() } returns baseDir
        every { environment.userHome() } returns homeDir
        every { fileSystemHandler.readFile(configurationPath) } returns fixtures("configuration")
    }

    private fun registerMocks() {
        ctx.registerSingleton(fileSystemHandler)
        ctx.registerSingleton(environment)
        ctx.registerSingleton(consoleHandler)
        ctx.registerSingleton(processExecutor)
    }

    fun execute(args: Array<String>) {
        PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)
    }

    fun setup(fileSystemSetup: PedreiroEnvironment.() -> Unit) {
        fileSystemSetup.invoke(this)
    }

    fun assertions(verifyContext: PedreiroEnvironment.() -> Unit) {
        verifyContext(this)
    }
}
