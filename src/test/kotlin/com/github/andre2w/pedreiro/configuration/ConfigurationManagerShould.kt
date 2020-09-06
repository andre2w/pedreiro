package com.github.andre2w.pedreiro.configuration

import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ConfigurationManagerShould {

    private val blueprintsFolder = "/home/pedreiro/.pedreiro/blueprints"
    private val configFilePath = "/home/pedreiro/.pedreiro/configuration.yml"
    private val configurationFile =
        """
        blueprintsFolder: "$blueprintsFolder"
        """.trimIndent()
    private val configuration = PedreiroConfiguration(blueprintsFolder)
    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val environment = mockk<LocalEnvironment>()
    private val consoleHandler = mockk<ConsoleHandler>(relaxUnitFun = true)

    @BeforeEach
    internal fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `retrieve configurations from the file system`() {
        every { environment.variable("PEDREIRO_CONFIG_PATH") } returns null
        every { fileSystemHandler.readFile(configFilePath) } returns configurationFile
        every { environment.userHome() } returns "/home/pedreiro"

        val configurationManager = ConfigurationManager(fileSystemHandler, environment, consoleHandler)
        val loadedConfiguration: PedreiroConfiguration = configurationManager.loadConfiguration()

        assertThat(loadedConfiguration).isEqualTo(configuration)
    }

    @Test
    fun `throw exception when configuration is not found`() {
        every { environment.variable("PEDREIRO_CONFIG_PATH") } returns null
        every { environment.userHome() } returns "/home/pedreiro"
        every { fileSystemHandler.readFile(configFilePath) } returns null

        val configurationManager = ConfigurationManager(fileSystemHandler, environment, consoleHandler)

        assertThrows<ConfigurationNotFound> {
            configurationManager.loadConfiguration()
        }
    }

    @Test
    internal fun `look for environment variable with configuration file path`() {
        every { environment.variable("PEDREIRO_CONFIG_PATH") } returns configFilePath
        every { fileSystemHandler.readFile(configFilePath) } returns configurationFile

        val configurationManager = ConfigurationManager(fileSystemHandler, environment, consoleHandler)
        val loadedConfiguration: PedreiroConfiguration = configurationManager.loadConfiguration()

        assertThat(loadedConfiguration).isEqualTo(configuration)
    }
}
