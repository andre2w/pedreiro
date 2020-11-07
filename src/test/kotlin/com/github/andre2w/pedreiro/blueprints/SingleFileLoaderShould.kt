package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.loaders.SingleFileLoader
import com.github.andre2w.pedreiro.configuration.ConfigurationManager
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SingleFileLoaderShould {

    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val configurationManager = mockk<ConfigurationManager>()
    private val configuration = PedreiroConfiguration("/home/user/pedreiro/.pedreiro/blueprints")
    private val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)
    private val blueprintTemplate =
            """
        ---
        - type: folder
          name: "{{ project_name }}"
          children:
            - type: command
              command: gradle init
        """.trimIndent()

    private val parsedTemplate =
            """
        ---
        - type: folder
          name: "test"
          children:
            - type: command
              command: gradle init
        """.trimIndent()

    private val singleFileLoader = SingleFileLoader(consoleHandler, fileSystemHandler)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
        every { configurationManager.loadConfiguration() } returns configuration
    }


    @Test
    fun `read yaml blueprint from file system parsing variables`() {
        val arguments = Arguments("test", mapOf("project_name" to "test"))
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.readFile("$filepath.yaml") } returns blueprintTemplate
        every { fileSystemHandler.isFolder(filepath) } returns false

        val blueprint = singleFileLoader.loadFrom(filepath, arguments)

        assertThat(blueprint).isEqualTo(Blueprint(parsedTemplate))
    }

    @Test
    fun `throw exception in case blueprint is not found`() {
        val filepath = "/home/user/pedreiro/.pedreiro/blueprints/test"
        every { fileSystemHandler.readFile("$filepath.yaml") } returns null
        every { fileSystemHandler.isFolder(filepath) } returns false

        assertThrows<BlueprintParsingException> {
            singleFileLoader.loadFrom(filepath, Arguments("blueprint"))
        }
    }

}