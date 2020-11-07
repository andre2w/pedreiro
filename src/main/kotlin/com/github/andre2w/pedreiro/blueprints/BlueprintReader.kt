package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.loaders.BlueprintLoader
import com.github.andre2w.pedreiro.blueprints.loaders.FolderLoader
import com.github.andre2w.pedreiro.blueprints.loaders.SingleFileLoader
import com.github.andre2w.pedreiro.configuration.ConfigurationManager
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.yaml.HandlebarsFactory
import javax.inject.Singleton

@Singleton
class BlueprintReader(
    private val fileSystemHandler: FileSystemHandler,
    private val consoleHandler: ConsoleHandler,
    private val configurationManager: ConfigurationManager,
    private val handlebarsFactory: HandlebarsFactory
) {

    fun read(arguments: Arguments): Blueprint {
        val configuration = configurationManager.loadConfiguration()
        val blueprintPath = "${configuration.blueprintsFolder}/${arguments.blueprintName}"
        return getLoader(blueprintPath).loadFrom(blueprintPath, arguments)
    }

    private fun getLoader(blueprintPath: String): BlueprintLoader =
            if (fileSystemHandler.isFolder(blueprintPath)) {
                FolderLoader(consoleHandler, fileSystemHandler, handlebarsFactory.withBaseFolder(blueprintPath))
            } else {
                SingleFileLoader(consoleHandler, fileSystemHandler)
            }
}
