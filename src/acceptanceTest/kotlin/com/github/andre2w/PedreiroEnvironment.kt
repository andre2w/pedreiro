package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import com.github.andre2w.pedreiro.yaml.HandlebarsFactory
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.mockk.every
import io.mockk.mockk

class PedreiroEnvironment(
    private val fixtures: FixtureLoader,
    val fileSystemHandler: FileSystemHandler = mockk(relaxUnitFun = true),
    val environment: LocalEnvironment = mockk(relaxUnitFun = true),
    val consoleHandler: ConsoleHandler = mockk(relaxUnitFun = true),
    val processExecutor: ProcessExecutor = mockk(relaxUnitFun = true),
    val baseDir: String = "/home/user/projects",
    val homeDir: String = "/home/user/pedreiro",
    val configurationPath: String = "$homeDir/.pedreiro/configuration.yaml",
    private val blueprintsPath: String = "/home/user/pedreiro/.pedreiro/blueprints"
) {

    private val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
    private val handlebarsFactory = mockk<HandlebarsFactory>()

    init {
        registerMocks()
        setupStubs()
    }

    private fun setupStubs() {
        every { environment.currentDir() } returns baseDir
        every { environment.userHome() } returns homeDir
        every { environment.variable("PEDREIRO_CONFIG_PATH") } returns null
        every { handlebarsFactory.withBaseFolder(any()) }.answers { Handlebars(ClassPathTemplateLoader(firstArg(), "")) }
        every { fileSystemHandler.readFile(configurationPath) } returns fixtures("configuration")
            .replace("BLUEPRINTS_PATH", blueprintsPath)
    }

    private fun registerMocks() {
        ctx.registerSingleton(fileSystemHandler)
        ctx.registerSingleton(environment)
        ctx.registerSingleton(consoleHandler)
        ctx.registerSingleton(processExecutor)
        ctx.registerSingleton(handlebarsFactory)
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
