package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.matchers.ignoringLineEnding
import io.mockk.every
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object BlueprintFromFolder : Spek({

    val fixtures = FixtureLoader("Folder", ".yml")
    val pedreiroEnvironemnt = PedreiroEnvironment(fixtures)

    describe("The Pedriero cli") {

        val blueprintName = "folder-blueprint"

        describe("when creating a project from a template in a folder") {
            pedreiroEnvironemnt.setup {
                val blueprintPath = "$homeDir/.pedreiro/blueprints/$blueprintName"
                every { fileSystemHandler.isFolder(blueprintPath) } returns true
                every { fileSystemHandler.readFile("$blueprintPath/blueprint.yml") } returns fixtures("template")
                every { fileSystemHandler.readFile("$blueprintPath/variables.yml") } returns fixtures("variables")
                every { fileSystemHandler.readFile("$blueprintPath/build.gradle") } returns fixtures("build_gradle_template.txt")
                every { fileSystemHandler.isFolder("$homeDir/.pedreiro/blueprints/$blueprintName") } returns true
                every { fileSystemHandler.listFilesIn("$homeDir/.pedreiro/blueprints/$blueprintName") } returns listOf("build.gradle")
            }

            pedreiroEnvironemnt.execute(arrayOf(blueprintName))

            it("should create files and folders with variables replaced") {
                pedreiroEnvironemnt.assertions {
                    verify {
                        fileSystemHandler.createFolder("$baseDir/test")
                        fileSystemHandler.createFolder("$baseDir/test/src")
                        fileSystemHandler.createFolder("$baseDir/test/src/main")
                        fileSystemHandler.createFolder("$baseDir/test/src/main/kotlin")
                        fileSystemHandler.createFolder("$baseDir/test/src/main/resources")
                        fileSystemHandler.createFile("$baseDir/test/build.gradle", ignoringLineEnding(fixtures("build_gradle_content.txt")))
                    }
                }
            }
        }
    }
})
