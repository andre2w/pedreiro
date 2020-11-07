package com.github.andre2w.pedreiro.blueprints.loaders

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.Blueprint
import com.github.andre2w.pedreiro.blueprints.BlueprintParsingException
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.jknack.handlebars.Handlebars
import org.yaml.snakeyaml.Yaml

class FolderLoader(
        private val consoleHandler: ConsoleHandler,
        private val fileSystemHandler: FileSystemHandler
) : BlueprintLoader {

    private val yaml = Yaml()
    private val handlebars = Handlebars()

     private val excludedFiles = setOf("blueprint.yml", "variables.yml")

    override fun loadFrom(path: String, arguments: Arguments): Blueprint {
        val mergedArguments = readVariables(path, arguments)

        val blueprint = loadFromFile("$path/blueprint", mergedArguments)
        val extraFiles = loadExtraFiles(path, mergedArguments)

        return Blueprint(blueprint, extraFiles)
    }

    private fun loadFromFile(blueprintPath: String, arguments: Arguments): String {
        val blueprint = readFile("$blueprintPath.yml")
                ?: readFile("$blueprintPath.yaml")
                ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

        consoleHandler.print("Creating project from blueprint ${blueprint.second}")

        return parseTemplate(blueprint.first, arguments)
    }

    private fun readVariables(blueprintPath: String, arguments: Arguments): Arguments {
        return try {
            val variablesFile = loadFromFile("$blueprintPath/variables", arguments)
            val variables = yaml.load<Map<String, String>>(variablesFile)
            arguments.mergeWith(variables)
        } catch (err: BlueprintParsingException) {
            arguments
        }
    }

    private fun loadExtraFiles(blueprintPath: String, arguments: Arguments): Map<String, String> {
        val extraFiles = fileSystemHandler.listFilesIn(blueprintPath)

        return extraFiles.asSequence()
                .filter { file -> file !in excludedFiles}
                .map { file -> readExtraFile(blueprintPath, file, arguments) }
                .toMap()
    }

    private fun readExtraFile(blueprintPath: String, file: String, arguments: Arguments): Pair<String, String> {
        val extraFileTemplate = fileSystemHandler.readFile("$blueprintPath/$file")
                ?: throw BlueprintParsingException("Failed to read file $file")

        val extraFile = parseTemplate(extraFileTemplate, arguments)

        return Pair(file, extraFile)
    }

    private fun parseTemplate(blueprintTemplate: String, arguments: Arguments) : String =
        handlebars
            .compileInline(blueprintTemplate)
            .apply(arguments.variables)

    private fun readFile(blueprintPath: String): Pair<String, String>? {
        consoleHandler.printDebug("Reading from file: $blueprintPath")
        return fileSystemHandler.readFile(blueprintPath)?.let {
            Pair(it, blueprintPath)
        }
    }


}