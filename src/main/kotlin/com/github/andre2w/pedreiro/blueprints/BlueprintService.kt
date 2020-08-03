package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.configuration.ConfigurationManager
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.*
import com.github.andre2w.pedreiro.yaml.YamlNode
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.parser.ParserException
import javax.inject.Singleton

sealed class ParseResult {
    data class Single(val task: Task) : ParseResult()
    data class Many(val tasks: List<Task>) : ParseResult()
}

@Singleton
class BlueprintService(
        private val blueprintReader: BlueprintReader,
        private val fileSystemHandler: FileSystemHandler,
        private val environment: LocalEnvironment,
        private val processExecutor: ProcessExecutor,
        private val configurationManager: ConfigurationManager,
        private val consoleHandler: ConsoleHandler
) {

    private val yamlParser: Yaml = Yaml()

    fun loadBlueprint(arguments: Arguments): Tasks {
        val configuration = configurationManager.loadConfiguration()
        val blueprint = blueprintReader.read(arguments, configuration)

        val blueprintTasks = try {
            yamlParser.load<Any>(blueprint.tasks)
        } catch (err: ParserException) {
            throw BlueprintParsingException("Failed to parse blueprint ${arguments.blueprintName}")
        }

        return Tasks.from(parse(YamlNode(blueprintTasks), blueprint))
    }

    private fun parse(
            yamlNode: YamlNode,
            blueprint: Blueprint,
            level: List<String> = ArrayList()
    ): List<Task> {

        if (yamlNode.isArray()) {
            return parseList(level, blueprint, yamlNode)
        }

        val result = ArrayList<Task>()
        val parsedResult = when (yamlNode.getTextFromField("type")) {
            "command" -> parseCommand(level.asPath(), yamlNode)
            "file" -> parseCreateFile(level.asPath(), blueprint, yamlNode)
            "folder" -> parseCreateFolder(level, blueprint, yamlNode)
            else -> throw BlueprintParsingException("Invalid type of ${yamlNode.getTextFromField("type")}")
        }

        when (parsedResult) {
            is ParseResult.Many -> result.addAll(parsedResult.tasks)
            is ParseResult.Single -> result.add(parsedResult.task)
        }

        return result
    }

    private fun parseList(
            level: List<String>,
            blueprint: Blueprint,
            yamlNode: YamlNode
    ): List<Task> {
        return yamlNode.flatMap { node -> parse(node, blueprint, level) }
    }

    private fun parseCreateFolder(
            level: List<String>,
            blueprint: Blueprint,
            yamlNode: YamlNode
    ): ParseResult.Many {
        val result = ArrayList<Task>()

        val currentLevel = level + yamlNode.getTextFromField("name")

        result.add(
                CreateFolder(
                        currentLevel.asPath(),
                        fileSystemHandler,
                        environment,
                        consoleHandler
                )
        )

        yamlNode.getChildren("children")?.let { children ->
            result.addAll(parseList(currentLevel, blueprint, children))
        }

        return ParseResult.Many(result)
    }

    private fun parseCreateFile(
            path: String,
            blueprint: Blueprint,
            yamlNode: YamlNode
    ): ParseResult.Single {

        val filePath = if (path == "") {
            yamlNode.getTextFromField("name")
        } else {
            path + "/" + yamlNode.getTextFromField("name")
        }

        val content = if (yamlNode.hasField("content")) {
            yamlNode.getTextFromField("content")
        } else {
            blueprint.fileContentOf(yamlNode.getTextFromField("source"))
        }

        val createFile = CreateFile(
                filePath,
                content,
                fileSystemHandler,
                environment,
                consoleHandler
        )

        return ParseResult.Single(createFile)
    }

    private fun parseCommand(path: String, yamlNode: YamlNode): ParseResult.Single {
        return ParseResult.Single(ExecuteCommand(yamlNode.getTextFromField("command"), path, processExecutor, environment))
    }

    private fun List<String>.asPath() = this.joinToString("/")
}

