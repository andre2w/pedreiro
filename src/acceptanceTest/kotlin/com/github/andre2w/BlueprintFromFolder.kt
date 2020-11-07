package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.matchers.ignoringLineEnding
import io.mockk.every
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object BlueprintFromFolder : Spek({

    val fixtures = FixtureLoader("Folder", ".yaml")
    val blueprintName = "folder-blueprint"
    val blueprintsFolder = "/Fixtures"
    val blueprintPath = "$blueprintsFolder/$blueprintName"
    val pedreiroEnvironemnt = PedreiroEnvironment(fixtures, blueprintsPath = blueprintsFolder)

    describe("The Pedriero cli") {

        describe("when creating a project from a template in a folder") {
            pedreiroEnvironemnt.setup {
                every { fileSystemHandler.isFolder(blueprintPath) } returns true
                every { fileSystemHandler.listFilesIn(blueprintPath) } returns
                        listOf("$blueprintPath/build.gradle", "$blueprintPath/variables.yaml", "$blueprintPath/blueprint.yaml")
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
