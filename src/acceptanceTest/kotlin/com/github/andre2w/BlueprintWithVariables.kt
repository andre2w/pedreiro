package com.github.andre2w

import com.github.andre2w.fixtures.FixtureLoader
import com.github.andre2w.matchers.ignoringLineEnding
import io.mockk.every
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object BlueprintWithVariables : Spek({

    val fixtures = FixtureLoader("Simple", ".yml")
    val pedreiroEnvironment = PedreiroEnvironment(fixtures)

    describe("The Pedreiro cli") {

        val blueprintName = "baseGradle"
        val blueprintPath = "${pedreiroEnvironment.homeDir}/.pedreiro/blueprints/${blueprintName}.yml"

        describe("createting a project form a blueprint with variables") {
            pedreiroEnvironment.setup {
                every { fileSystemHandler.isFolder("$homeDir/.pedreiro/blueprints/$blueprintName") } returns false
                every { fileSystemHandler.readFile(blueprintPath) } returns fixtures("template_with_variables")
            }

            pedreiroEnvironment.execute(arrayOf(blueprintName, "-a", "project_name=new-project", "--arg", "package_name=com.test"))

            it("should create resources with values passed as parameter") {
                pedreiroEnvironment.assertions {
                    verify {
                        fileSystemHandler.createFolder("$baseDir/new-project")
                        fileSystemHandler.createFile("$baseDir/new-project/build.gradle", ignoringLineEnding(fixtures("build_gradle_with_variable.txt")))
                    }
                }
            }
        }
    }
})