package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.*
import com.github.andre2w.pedreiro.yaml.InvalidNodeType
import com.github.andre2w.pedreiro.yaml.YamlNode
import com.github.andre2w.pedreiro.yaml.YamlParser
import org.yaml.snakeyaml.parser.ParserException
import java.util.*
import javax.inject.Singleton

@Singleton
class BlueprintService(
    private val blueprintReader: BlueprintReader,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: LocalEnvironment,
    private val processExecutor: ProcessExecutor,
    private val consoleHandler: ConsoleHandler,
    private val yamlParser: YamlParser
) {

    fun loadBlueprint(arguments: Arguments): Tasks {
        val blueprint = blueprintReader.read(arguments)

        val blueprintTasks = try {
            yamlParser.parse(blueprint.tasks)
        } catch (err: ParserException) {
            throw BlueprintParsingException("Failed to parse blueprint ${arguments.blueprintName}")
        }

        val parseableObjects = parseableObject(blueprintTasks)

        return parseableObjects
            .map { parseObject(it.node, it.level, blueprint) }
            .let(Tasks.Companion::from)
    }

    private fun parseObject(yamlNode: YamlNode.Object, level: List<String>, blueprint: Blueprint): Task {
        return when (val objectType = yamlNode.textValue("type")) {
            "command" -> parseCommand(level.asPath(), yamlNode)
            "file" -> parseCreateFile(level.asPath(), blueprint, yamlNode)
            "folder" -> parseCreateFolder(level, yamlNode)
            else -> throw BlueprintParsingException("Invalid type of $objectType")
        }
    }

    private fun parseCreateFolder(level: List<String>, yamlNode: YamlNode.Object): Task {
        val currentLevel = level + yamlNode.textValue("name")
        return createFolderWith(currentLevel)
    }

    private fun parseCreateFile(path: String, blueprint: Blueprint, yamlNode: YamlNode.Object): Task {
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

        return createFileWith(filePath, content)
    }

    private fun parseCommand(path: String, yamlNode: YamlNode.Object): Task {
        val platform = consoleHandler.currentPlatform()
        val command = when (val node = yamlNode[platform.shortName]) {
            is YamlNode.Value -> node.asText()
            else -> yamlNode.textValue("command")
        }
        return ExecuteCommand(command, path, processExecutor, environment)
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
        return CreateFile(filePath, content, fileSystemHandler, environment, consoleHandler)
    }

    private fun List<String>.asPath() = this.joinToString("/")

    private fun YamlNode.Object.textValue(field: String): String {
        return when (val typeNode = this[field]) {
            is YamlNode.Value -> typeNode.asText()
            else -> throw InvalidNodeType("Type must contain a string, found: $typeNode")
        }
    }

    private fun parseableObject(node: YamlNode, level: List<String> = emptyList()): List<ParseableObject> {
        val result = LinkedList<ParseableObject>()

        val list = when (node) {
            is YamlNode.Object -> parseSingleObject(node, level)
            is YamlNode.List -> node.flatMap { parseableObject(it, level) }
            else -> emptyList()
        }

        result.addAll(list)

        return result
    }

    private fun parseSingleObject(node: YamlNode.Object, level: List<String>): List<ParseableObject> {
        val result = LinkedList<ParseableObject>()

        result.add(ParseableObject(level, node))

        if (node.textValue("type") == "folder") {
            val children = when (val child = node["children"]) {
                is YamlNode.List -> child.flatMap { parseableObject(it, level + node.textValue("name")) }
                else -> emptyList()
            }
            result.addAll(children)
        }

        return result
    }
}

data class ParseableObject(
    val level: List<String>,
    val node: YamlNode.Object
)
