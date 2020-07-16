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

object BlueprintFromFolder : Spek({

    val fixtures = FixtureLoader("Folder", ".yml")
    val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)

    describe("The Pedriero cli") {
        val baseDir = "/home/user/projects"
        val homeDir = "/home/user/pedreiro"
        val blueprintName = "folder-blueprint"
        val configurationPath = "$homeDir/.pedreiro/configuration.yml"
        val blueprintPath = "$homeDir/.pedreiro/blueprints/${blueprintName}"

        val fileSystemHandler = mockk<FileSystemHandler>(relaxUnitFun = true)
        val environment = mockk<LocalEnvironment>(relaxUnitFun = true)
        val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
        val processExecutor = mockk<ProcessExecutor>(relaxUnitFun = true)
        ctx.registerSingleton(fileSystemHandler)
        ctx.registerSingleton(environment)
        ctx.registerSingleton(consoleHandler)
        ctx.registerSingleton(processExecutor)



        describe("when creating a project from a template in a folder") {
            every { environment.currentDir() } returns baseDir
            every { environment.userHome() } returns homeDir
            every { fileSystemHandler.readFile(configurationPath) } returns fixtures("configuration")
            every { fileSystemHandler.isFolder(blueprintPath) } returns true
            every { fileSystemHandler.readFile("$blueprintPath/blueprint.yml") } returns fixtures("template")
            every { fileSystemHandler.readFile("$blueprintPath/variables.yml") } returns fixtures("variables")
            every { fileSystemHandler.readFile("$blueprintPath/build.gradle") } returns fixtures("build_gradle_template")
            every { fileSystemHandler.isFolder("$homeDir/.pedreiro/blueprints/$blueprintName") } returns true
            every { fileSystemHandler.listFilesIn("$homeDir/.pedreiro/blueprints/$blueprintName") } returns listOf("build.gradle")

            execute(ctx, arrayOf(blueprintName))

            it("should create files and folders with variables replaced") {
                verify {
                    fileSystemHandler.createFolder("$baseDir/test")
                    fileSystemHandler.createFolder("$baseDir/test/src")
                    fileSystemHandler.createFolder("$baseDir/test/src/main")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/kotlin")
                    fileSystemHandler.createFolder("${baseDir}/test/src/main/resources")
                    fileSystemHandler.createFile("${baseDir}/test/build.gradle", ignoringLineEnding(fixtures("build_gradle_content")))
                }
            }
        }
    }

})

private fun execute(ctx: ApplicationContext, args: Array<String>) {
    PicocliRunner.run(PedreiroCommand::class.java, ctx, *args)
}