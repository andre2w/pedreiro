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

     private val excludedFiles = setOf("blueprint.yaml", "variables.yaml")

    override fun loadFrom(path: String, arguments: Arguments): Blueprint {
        val mergedArguments = readVariables(path, arguments)
        consoleHandler.printDebug("Reading from folder: $path")
        val blueprint = readAndParse(path, "blueprint.yaml", mergedArguments)
        consoleHandler.print("Creating project from blueprint folder $path")
        val extraFiles = loadExtraFiles(path, mergedArguments)
        return Blueprint(blueprint, extraFiles)
    }

    private fun readVariables(blueprintPath: String, arguments: Arguments): Arguments {
        return try {
            val variablesFile = readAndParse(blueprintPath, "variables.yaml", arguments)
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
                .map { file -> Pair(file, readAndParse(blueprintPath, file, arguments)) }
                .onEach { file -> consoleHandler.printDebug("Loaded ${file.first} from ${file.second}") }
                .toMap()
    }

    private fun readAndParse(blueprintPath: String, file: String, arguments: Arguments): String {
        val extraFileTemplate = fileSystemHandler.readFile("$blueprintPath/$file")
                ?: throw BlueprintParsingException("Failed to read file $file")
        return parseTemplate(extraFileTemplate, arguments)
    }

    private fun parseTemplate(blueprintTemplate: String, arguments: Arguments) : String =
        handlebars
            .compileInline(blueprintTemplate)
            .apply(arguments.variables)
}