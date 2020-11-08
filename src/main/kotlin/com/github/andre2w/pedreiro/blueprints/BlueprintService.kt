package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.tasks.Tasks
import com.github.andre2w.pedreiro.yaml.InvalidNodeType
import com.github.andre2w.pedreiro.yaml.YamlNode
import com.github.andre2w.pedreiro.yaml.YamlParser
import org.yaml.snakeyaml.parser.ParserException
import java.util.LinkedList
import javax.inject.Singleton

@Singleton
class BlueprintService(
    private val blueprintReader: BlueprintReader,
    private val yamlParser: YamlParser,
    private val taskFactory: TaskFactory
) {

    fun loadBlueprint(arguments: Arguments): Tasks {
        val blueprint = blueprintReader.read(arguments)
        val blueprintTasks = try {
            yamlParser.parse(blueprint.tasks)
        } catch (err: ParserException) {
            throw BlueprintParsingException("Failed to parse blueprint ${arguments.blueprintName}")
        }
        return toParsingObject(blueprintTasks)
            .map { taskFactory.create(it.node, it.level, blueprint) }
            .let(Tasks.Companion::of)
    }

    private fun YamlNode.Object.textValue(field: String): String {
        return when (val typeNode = this[field]) {
            is YamlNode.Value -> typeNode.asText()
            else -> throw InvalidNodeType("Type must contain a string, found: $typeNode")
        }
    }

    private fun toParsingObject(node: YamlNode, level: List<String> = emptyList()): List<ParsingObject> {
        val result = LinkedList<ParsingObject>()
        val list = when (node) {
            is YamlNode.Object -> parseSingleObject(node, level)
            is YamlNode.List -> node.flatMap { toParsingObject(it, level) }
            else -> emptyList()
        }
        result.addAll(list)
        return result
    }

    private fun parseSingleObject(node: YamlNode.Object, level: List<String>): List<ParsingObject> {
        val result = LinkedList<ParsingObject>()
        result.add(ParsingObject(level, node))

        if (node.textValue("type") == "folder") {
            val children = when (val child = node["children"]) {
                is YamlNode.List -> child.flatMap { toParsingObject(it, level + node.textValue("name")) }
                else -> emptyList()
            }
            result.addAll(children)
        }

        return result
    }
}

data class ParsingObject(
    val level: List<String>,
    val node: YamlNode.Object
)
