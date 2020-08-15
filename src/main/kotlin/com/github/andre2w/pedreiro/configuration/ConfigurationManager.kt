package com.github.andre2w.pedreiro.configuration

import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import org.yaml.snakeyaml.Yaml
import java.nio.file.Paths
import javax.inject.Singleton

@Singleton
class ConfigurationManager(
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment,
        private val consoleHandler: ConsoleHandler
) {

    private val yaml = Yaml()

    fun loadConfiguration(): PedreiroConfiguration {
        val configFilePath = environment.userHome() + "/.pedreiro/configuration.yml"

        consoleHandler.printDebug("Reading configuration from file: $configFilePath")

        val configuration = fileSystemHandler.readFile(configFilePath)
                ?: throw ConfigurationNotFound(normalizePath(configFilePath))

        val loadedConfig = yaml.load<Map<String, String>>(configuration)

        return PedreiroConfiguration(loadedConfig["blueprintsFolder"]!!)
    }

    private fun normalizePath(configFilePath: String) =
        Paths.get(configFilePath).normalize().toString()
}
