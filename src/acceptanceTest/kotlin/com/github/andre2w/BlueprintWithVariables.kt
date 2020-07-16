package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.matchers.ignoringLineEnding
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object BlueprintWithVariables : Spek({

    val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
    val fixtures = FixtureLoader("Simple", ".yml")

    describe("The Pedreiro cli") {
        val baseDir = "/home/user/projects"
        val homeDir = "/home/user/pedreiro"
        val blueprintName = "baseGradle"
        val configurationPath = "$homeDir/.pedreiro/configuration.yml"
        val blueprintPath = "$homeDir/.pedreiro/blueprints/${blueprintName}.yml"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<LocalEnvironment>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)
        ctx.registerSingleton(fileSystemHandler)
        ctx.registerSingleton(environment)
        ctx.registerSingleton(consoleHandler)
        ctx.registerSingleton(processExecutor)

        every { fileSystemHandler.isFolder("$homeDir/.pedreiro/blueprints/$blueprintName") } returns false

        describe("createting a project form a blueprint with variables") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns fixtures("configuration")
            every { fileSystemHandler.readFile(blueprintPath) } returns fixtures("template_with_variables")

            execute(ctx, arrayOf(blueprintName, "-a", "project_name=new-project", "--arg", "package_name=com.test"))

            it("should create resources with values passed as parameter") {
                verify {
                    fileSystemHandler.createFolder("$baseDir/new-project")
                    fileSystemHandler.createFile("$baseDir/new-project/build.gradle", ignoringLineEnding(fixtures("build_gradle_with_variable")))
                }
            }
        }
    }
})

private fun execute(ctx: ApplicationContext, args: Array<String>) {
    PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)
}