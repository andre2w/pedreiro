package com.github.andre2w.pedreiro.blueprints.loaders

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.Blueprint
import com.github.andre2w.pedreiro.blueprints.BlueprintParsingException
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.jknack.handlebars.Handlebars


class SingleFileLoader(
        private val consoleHandler: ConsoleHandler,
        private val fileSystemHandler: FileSystemHandler,
        private val handlebars: Handlebars = Handlebars()
) : BlueprintLoader {

    override fun loadFrom(path: String, arguments: Arguments): Blueprint {
        consoleHandler.printDebug("Reading from file: ${path}.yaml")

        val blueprint = fileSystemHandler.readFile("$path.yaml")
                ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

        consoleHandler.print("Creating project from blueprint ${path}.yaml")

        return handlebars
                .compileInline(blueprint)
                .apply(arguments.variables)
                .let { compiledTemplate -> Blueprint(compiledTemplate) }
    }
}