package com.github.andre2w.pedreiro.configuration

import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConfigurationManagerShould {

    private val blueprintsFolder = "/home/pedreiro/.pedreiro/blueprints"
    private val configFilePath = "/home/pedreiro/.pedreiro/configuration.yml"
    private val configurationFile = """
        blueprintsFolder: "$blueprintsFolder"
    """.trimIndent()
    private val configuration = PedreiroConfiguration(blueprintsFolder)
    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val environment = mockk<LocalEnvironment>()

    @Test
    fun `retrieve configurations from the file system`() {
        every { fileSystemHandler.readFile(configFilePath) } returns configurationFile
        every { environment.userHome() } returns "/home/pedreiro"

        val configurationManager = ConfigurationManager(fileSystemHandler, environment)
        val loadedConfiguration : PedreiroConfiguration = configurationManager.loadConfiguration()

        assertThat(loadedConfiguration).isEqualTo(configuration)
    }

    @Test
    fun `throw exception when configuration is not found`() {
        every { environment.userHome() } returns "/home/pedreiro"
        every { fileSystemHandler.readFile(configFilePath) } returns null

        val configurationManager = ConfigurationManager(fileSystemHandler, environment)

        assertThrows<ConfigurationNotFound> {
            configurationManager.loadConfiguration()
        }
    }
}