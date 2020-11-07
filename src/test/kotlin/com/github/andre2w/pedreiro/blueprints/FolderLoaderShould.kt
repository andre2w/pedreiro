package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.loaders.FolderLoader
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FolderLoaderShould {

    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)

    private val parsedTemplate =
        """
        ---
        - type: folder
          name: "test"
          children:
            - type: folder
              name: main
              children:
                - type: folder
                  name: kotlin

            - type: folder
              name: main
              children:
                - type: folder
                  name: kotlin
        
            - type: command
              command: gradle init
        """.trimIndent().lines().joinToString(System.lineSeparator())

    val parsedBuildGradle = """
        plugin {
            id 'kotlin' version: 1.4.10
        }
    """.trimIndent().lines().joinToString(System.lineSeparator())

    private val folderLoader = FolderLoader(consoleHandler, fileSystemHandler)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `read blueprint parsing templates and extra files`() {
        val path = "/templates/happy_path"
        every { fileSystemHandler.listFilesIn(path) } returns listOf(
            "/templates/happy_path/blueprint.yaml",
            "/templates/happy_path/build_gradle.txt",
            "/templates/happy_path/variables.yaml",
        )
        val handlebars = Handlebars(ClassPathTemplateLoader(path, ""))
        val folderLoader = FolderLoader(consoleHandler, fileSystemHandler, handlebars)
        val arguments = Arguments("blueprint", mapOf("project_name" to "test"))

        val blueprint =
            folderLoader.loadFrom(path, arguments)

        assertThat(blueprint.tasks).isEqualTo(parsedTemplate)
        assertThat(blueprint.fileContentOf("build_gradle.txt")).isEqualTo(parsedBuildGradle)
    }

    @Test
    fun `throw exception when blueprint yaml is not available`() {
        val path = "/templates/missing_blueprint"
        every { fileSystemHandler.listFilesIn(path) } returns listOf(
            "/templates/happy_path/blueprint.yaml",
            "/templates/happy_path/build_gradle.txt",
            "/templates/happy_path/variables.yaml",
        )
        val handlebars = Handlebars(ClassPathTemplateLoader(path, ""))
        val folderLoader = FolderLoader(consoleHandler, fileSystemHandler, handlebars)
        val arguments = Arguments("blueprint", mapOf("project_name" to "test"))

        assertThrows<BlueprintParsingException> { folderLoader.loadFrom(path, arguments) }
    }

    @Test
    internal fun `throw exception when extra file does not exist`() {
        val path = "/templates/missing_blueprint"
        every { fileSystemHandler.listFilesIn(path) } returns listOf(
            "/templates/happy_path/missing.yaml"
        )
        val handlebars = Handlebars(ClassPathTemplateLoader(path, ""))
        val folderLoader = FolderLoader(consoleHandler, fileSystemHandler, handlebars)
        val arguments = Arguments("blueprint", mapOf("project_name" to "test"))

        assertThrows<BlueprintParsingException> { folderLoader.loadFrom(path, arguments) }
    }
}
