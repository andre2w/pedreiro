package com.github.andre2w.pedreiro.blueprints.loaders

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.Blueprint
import com.github.andre2w.pedreiro.blueprints.BlueprintParsingException
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.jknack.handlebars.Handlebars
import org.yaml.snakeyaml.Yaml
import java.io.FileNotFoundException

class FolderLoader(
        private val consoleHandler: ConsoleHandler,
        private val fileSystemHandler: FileSystemHandler,
        private val handlebars: Handlebars = Handlebars()
) : BlueprintLoader {

    private val yaml = Yaml()

    private val excludedFiles = setOf("blueprint.yaml", "variables.yaml")

    override fun loadFrom(path: String, arguments: Arguments): Blueprint {
        val mergedArguments = readVariables(arguments)
        consoleHandler.printDebug("Reading from folder: $path")
        val blueprint = readAndParse("blueprint.yaml", mergedArguments)
        consoleHandler.print("Creating project from blueprint folder $path")
        val extraFiles = loadExtraFiles(path, mergedArguments)
        return Blueprint(blueprint, extraFiles)
    }

    private fun readVariables(arguments: Arguments): Arguments {
        return try {
            val variablesFile = readAndParse("variables.yaml", arguments)
            val variables = yaml.load<Map<String, String>>(variablesFile)
            arguments.mergeWith(variables)
        } catch (err: BlueprintParsingException) {
            arguments
        }
    }

    private fun loadExtraFiles(blueprintPath: String, arguments: Arguments): Map<String, String> {
        val extraFiles = fileSystemHandler.listFilesIn(blueprintPath)
        return extraFiles.asSequence()
                .filter { file -> file !in excludedFiles }
                .map { file -> removeBlueprintPath(file, blueprintPath) }
                .map { file -> Pair(file, readAndParse(file, arguments)) }
                .onEach { file -> consoleHandler.printDebug("Loaded ${file.first} from ${file.second}") }
                .toMap()
    }

    private fun removeBlueprintPath(file: String, blueprintPath: String) =
            file.substring(blueprintPath.length + 1)

    private fun readAndParse(file: String, arguments: Arguments): String {
        try {
            return handlebars.compile(file).apply(arguments.variables)
        } catch (err: FileNotFoundException) {
            throw BlueprintParsingException("Failed to load the file: $file")
        }
    }

}