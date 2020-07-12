package com.github.andre2w.configuration

import com.github.andre2w.environment.FileSystemHandler
import com.github.andre2w.environment.LocalEnvironment
import io.micronaut.context.annotation.Factory
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import javax.inject.Singleton

@Factory
class ConfigurationManager(
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment
) {

    private val yaml = Yaml()

    @Singleton
    fun pedreiroConfiguration(): PedreiroConfiguration {
        val configFilePath = environment.userHome() + "/.pedreiro/configuration.yml"

        val configuration = fileSystemHandler.readFile(configFilePath)
                ?: throw ConfigurationNotFound(configFilePath)

        val loadedConfig = yaml.load<Map<String, String>>(configuration)

        return PedreiroConfiguration(loadedConfig["blueprintsFolder"]!!)
    }

}
