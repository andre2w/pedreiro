package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.matchers.ignoringLineEnding
import io.mockk.every
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StartingSimpleProject : Spek({

    val fixtures = FixtureLoader("Simple", ".yml")
    val pedreiroEnvironment = PedreiroEnvironment(fixtures)

    describe("The Pedreiro cli") {

        val blueprintName = "baseGradle"
        val blueprintPath = "${pedreiroEnvironment.homeDir}/.pedreiro/blueprints/$blueprintName.yml"

        describe("creating project from a simple blueprint with only folders and files") {
            pedreiroEnvironment.setup {
                every { fileSystemHandler.isFolder("$homeDir/.pedreiro/blueprints/$blueprintName") } returns false
                every { environment.currentDir() } returns baseDir
                every { environment.userHome() } returns homeDir
                every { fileSystemHandler.readFile(configurationPath) } returns fixtures("configuration")
                every { fileSystemHandler.readFile(blueprintPath) } returns fixtures("simple_template")
            }

            pedreiroEnvironment.execute(arrayOf(blueprintName))

            it("should create the file structure declared in the blueprint") {
                pedreiroEnvironment.assertions {
                    verify {
                        fileSystemHandler.createFolder("$baseDir/test/src/main/kotlin")
                        fileSystemHandler.createFolder("$baseDir/test/src/main/resources")
                        fileSystemHandler.createFile("$baseDir/test/src/build.gradle", ignoringLineEnding(fixtures("build_gradle_content.txt")))
                    }
                }
            }

            it("should print information about template creation and when its done") {
                pedreiroEnvironment.assertions {
                    verify {
                        consoleHandler.print("Creating project from blueprint $blueprintPath")
                        consoleHandler.print("Project created. You can start to work now.")
                        consoleHandler.exitWith(0)
                    }
                }
            }
        }

        describe("create a project executing a command") {
            val command = listOf(
                    "gradle",
                    "init",
                    "--type",
                    "java-application",
                    "--test-framework",
                    "junit",
                    "--dsl",
                    "groovy",
                    "--project-name",
                    "test",
                    "--package",
                    "com.example.test"
            ).joinToString(" ")

            pedreiroEnvironment.setup {
                every { fileSystemHandler.readFile(blueprintPath) } returns fixtures("command_template")
                every { processExecutor.execute(command, "$baseDir/test") } returns 0
            }

            pedreiroEnvironment.execute(arrayOf(blueprintName))

            it("should create a folder and execute command inside") {
                pedreiroEnvironment.assertions {
                    verify {
                        fileSystemHandler.createFolder("$baseDir/test")
                        processExecutor.execute(command, "$baseDir/test")
                    }
                }
            }
        }

        describe("when creating a project from a blueprint that doesn't exists") {
            pedreiroEnvironment.setup {
                every { fileSystemHandler.readFile(blueprintPath) } returns null
                every { fileSystemHandler.readFile("$homeDir/.pedreiro/blueprints/$blueprintName.yaml") } returns null
            }

            pedreiroEnvironment.execute(arrayOf(blueprintName))

            it("should display message saying that template was not found") {
                pedreiroEnvironment.assertions {
                    verify { consoleHandler.printError("Failed to read blueprint $blueprintName") }
                }
            }

            it("should exit with status code of 1") {
                pedreiroEnvironment.assertions {
                    verify { consoleHandler.exitWith(1) }
                }
            }
        }

        describe("when creating a project from a invalid blueprint") {
            pedreiroEnvironment.setup {
                every { fileSystemHandler.readFile(blueprintPath) } returns "INVALID TEMPLATE"
            }

            it("should display message saying that failed to load blueprint") {
                pedreiroEnvironment.assertions {
                    verify { consoleHandler.printError("Failed to read blueprint $blueprintName") }
                }
            }

            it("should exit with status code 1") {
                pedreiroEnvironment.assertions {
                    verify { consoleHandler.exitWith(1) }
                }
            }
        }
    }
})
