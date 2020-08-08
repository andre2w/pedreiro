package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.configuration.ConfigurationManager
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.*
import com.github.andre2w.pedreiro.yaml.InvalidNodeType
import com.github.andre2w.pedreiro.yaml.YamlNode
import com.github.andre2w.pedreiro.yaml.YamlParser
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
        private val consoleHandler: ConsoleHandler,
        private val yamlParser: YamlParser
) {

    fun loadBlueprint(arguments: Arguments): Tasks {
        val configuration = configurationManager.loadConfiguration()
        val blueprint = blueprintReader.read(arguments, configuration)

        val blueprintTasks = try {
            yamlParser.parse(blueprint.tasks)
        } catch (err: ParserException) {
            throw BlueprintParsingException("Failed to parse blueprint ${arguments.blueprintName}")
        }

        return Tasks.from(parse(blueprintTasks, blueprint))
    }

    private fun parse(
            yamlNode: YamlNode,
            blueprint: Blueprint,
            level: List<String> = ArrayList()
    ): List<Task> {
        return when (yamlNode) {
            is YamlNode.List -> parseList(level, blueprint, yamlNode)
            is YamlNode.Object -> parseObject(yamlNode, level, blueprint)
            else -> throw IllegalStateException()
        }
    }

    private fun parseObject(yamlNode: YamlNode.Object, level: List<String>, blueprint: Blueprint): List<Task> {

        val parsedResult = when (val objectType = yamlNode.textValue("type")) {
            "command" -> parseCommand(level.asPath(), yamlNode)
            "file" -> parseCreateFile(level.asPath(), blueprint, yamlNode)
            "folder" -> parseCreateFolder(level, blueprint, yamlNode)
            else -> throw BlueprintParsingException("Invalid type of $objectType")
        }

        return when (parsedResult) {
            is ParseResult.Many -> parsedResult.tasks
            is ParseResult.Single -> listOf(parsedResult.task)
        }
    }

    private fun parseList(
            level: List<String>,
            blueprint: Blueprint,
            yamlNode: YamlNode.List
    ): List<Task> {
        return yamlNode.flatMap { node -> parse(node, blueprint, level) }
    }

    private fun parseCreateFolder(
            level: List<String>,
            blueprint: Blueprint,
            yamlNode: YamlNode.Object
    ): ParseResult.Many {
        val result = ArrayList<Task>()

        val currentLevel = level + yamlNode.textValue("name")

        result.add(createFolderWith(currentLevel))

        val childTasks = when (val children = yamlNode["children"]) {
            is YamlNode.List -> parseList(currentLevel, blueprint, children)
            is YamlNode.Missing -> emptyList()
            else -> throw InvalidNodeType("children field should have have a list")
        }
        result.addAll(childTasks)

        return ParseResult.Many(result)
    }

    private fun parseCreateFile(
            path: String,
            blueprint: Blueprint,
            yamlNode: YamlNode.Object
    ): ParseResult.Single {

        val filePath = if (path == "") {
            yamlNode.textValue("name")
        } else {
            path + "/" + yamlNode.textValue("name")
        }

        val content = when (val contentNode = yamlNode["content"]) {
            is YamlNode.Value -> contentNode.asText()
            is YamlNode.Missing -> blueprint.fileContentOf(yamlNode.textValue("source"))
            else -> throw InvalidNodeType("Folder type should have text in content field or source pointing to a file")
        }

        val createFile = createFileWith(filePath, content)

        return ParseResult.Single(createFile)
    }

    private fun parseCommand(path: String, yamlNode: YamlNode.Object): ParseResult.Single {
        return ParseResult.Single(ExecuteCommand(yamlNode.textValue("command"), path, processExecutor, environment))
    }

    private fun createFolderWith(currentLevel: List<String>): CreateFolder {
        return CreateFolder(
                currentLevel.asPath(),
                fileSystemHandler,
                environment,
                consoleHandler
        )
    }

    private fun createFileWith(filePath: String, content: String): CreateFile {
        return CreateFile(
                filePath,
                content,
                fileSystemHandler,
                environment,
                consoleHandler
        )
    }

    private fun List<String>.asPath() = this.joinToString("/")

    private fun YamlNode.Object.textValue(field: String): String {
        return when (val typeNode = this[field]) {
            is YamlNode.Value -> typeNode.asText()
            else -> throw InvalidNodeType("Type must contain a string, found: $typeNode")
        }
    }
}
