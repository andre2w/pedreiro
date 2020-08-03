package com.github.andre2w.pedreiro.yaml

import org.yaml.snakeyaml.Yaml

class YamlParser {

    private val yamlParser = Yaml()

    fun parse(yaml: String): YamlObject {
        val keyValues = yamlParser.load<Any>(yaml)
        return YamlObject(keyValues as Map<String, Any>)
    }

}
