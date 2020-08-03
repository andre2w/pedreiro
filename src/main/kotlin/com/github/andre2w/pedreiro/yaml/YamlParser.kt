package com.github.andre2w.pedreiro.yaml

import org.yaml.snakeyaml.Yaml

class YamlParser {

    private val yamlParser = Yaml()

    fun parse(yaml: String): YamlNodeNew {
        val keyValues = yamlParser.load<Any>(yaml)
        return YamlNodeNew.parse(keyValues)
    }

}
