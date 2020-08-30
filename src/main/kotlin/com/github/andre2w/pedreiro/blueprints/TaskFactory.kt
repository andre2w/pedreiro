package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.CreateFile
import com.github.andre2w.pedreiro.tasks.CreateFolder
import com.github.andre2w.pedreiro.tasks.ExecuteCommand
import com.github.andre2w.pedreiro.tasks.Task
import com.github.andre2w.pedreiro.yaml.InvalidNodeType
import com.github.andre2w.pedreiro.yaml.YamlNode
import javax.inject.Singleton

@Singleton
class TaskFactory(
    private val consoleHandler: ConsoleHandler,
    private val fileSystemHandler: FileSystemHandler,
    private val environment: LocalEnvironment,
    private val processExecutor: ProcessExecutor
) {

    fun create(yamlNode: YamlNode.Object, level: List<String>, blueprint: Blueprint): Task {
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
}
