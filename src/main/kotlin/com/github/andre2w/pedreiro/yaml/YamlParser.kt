package com.github.andre2w.pedreiro.yaml

import io.micronaut.context.annotation.Prototype
import org.yaml.snakeyaml.Yaml

@Prototype
class YamlParser {

    private val yaml = Yaml()

    fun parse(yamlFile: String): YamlNode {
        val keyValues = yaml.load<Any>(yamlFile)
        return YamlNode.parse(keyValues)
    }
}
