package com.github.andre2w.pedreiro.yaml

import io.micronaut.context.annotation.Prototype
import org.yaml.snakeyaml.Yaml

@Prototype
class YamlParser {

    private val yamlParser = Yaml()

    fun parse(yaml: String): YamlNode {
        val keyValues = yamlParser.load<Any>(yaml)
        return YamlNode.parse(keyValues)
    }

}
