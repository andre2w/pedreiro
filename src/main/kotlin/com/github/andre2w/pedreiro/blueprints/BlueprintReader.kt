package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.jknack.handlebars.Handlebars
import org.yaml.snakeyaml.Yaml
import javax.inject.Singleton

@Singleton
class BlueprintReader(
        private val fileSystemHandler: FileSystemHandler,
        private val consoleHandler: ConsoleHandler,
        private val configuration: PedreiroConfiguration
) {
    private val handlebars: Handlebars = Handlebars()

    fun read(arguments: Arguments): Blueprint {
        val blueprintPath = "${configuration.blueprintsFolder}/${arguments.blueprintName}"

        return if (fileSystemHandler.isFolder(blueprintPath)) {
            loadFromFolder(blueprintPath, arguments)
        } else {
            Blueprint(loadFromFile(blueprintPath, arguments))
        }
    }

    private fun loadFromFile(blueprintPath: String, arguments: Arguments): String {
        val blueprintTemplate = readFile("$blueprintPath.yml")
                ?: readFile("$blueprintPath.yaml")
                ?: throw BlueprintParsingException("Failed to read blueprint ${arguments.blueprintName}")

        return parseTemplate(blueprintTemplate, arguments)
    }

    private fun loadFromFolder(blueprintPath: String, arguments: Arguments): Blueprint {
        val mergedArguments = readVariables(blueprintPath, arguments)

        val blueprint = loadFromFile("$blueprintPath/blueprint", mergedArguments)
        val extraFiles = loadExtraFiles(blueprintPath, mergedArguments)

        return Blueprint(blueprint, extraFiles)
    }

    private fun readVariables(
            blueprintPath: String,
            arguments: Arguments
    ): Arguments {
        return try {
            val variablesFile = loadFromFile("$blueprintPath/variables", arguments)

            val yaml = Yaml()
            val variables = yaml.load<Map<String,String>>(variablesFile)
            arguments.mergeWith(variables)
        } catch (err : BlueprintParsingException) {
            arguments
        }
    }

    private fun loadExtraFiles(blueprintPath: String, arguments: Arguments): Map<String, String> {
        val extraFiles = fileSystemHandler.listFilesIn(blueprintPath)

        return extraFiles.asSequence()
                .filter { file -> file != "blueprint.yml" && file != "variables.yml"}
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
                    .apply(arguments.extraArguments)

    private fun readFile(blueprintPath: String) : String? {
        val blueprint = fileSystemHandler.readFile(blueprintPath)
        if (blueprint != null) {
            consoleHandler.print("Creating project from blueprint ($blueprintPath)")
        }
        return blueprint
    }

}