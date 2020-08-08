package com.github.andre2w.pedreiro.yaml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class YamlParserShould {

    private val yamlParser = YamlParser()

    @Test
    fun `parse object`() {
        val yaml = """
            field: test
        """.trimIndent()

        val parsedYaml = yamlParser.parse(yaml) as YamlNode.Object

        val expected = YamlNode.Object(mapOf(
                "field" to "test"
        ))
        assertThat(parsedYaml).isEqualTo(expected)
    }

    @Test
    internal fun `parse list of objects`() {
        val yaml = """
            - first_field: test
            - second_field: second value
        """.trimIndent()

        val parsedYaml = yamlParser.parse(yaml) as YamlNode.List

        val expected = YamlNode.List(listOf(
                YamlNode.Object(mapOf("first_field" to "test")),
                YamlNode.Object(mapOf("second_field" to "second value"))
        ))
        assertThat(parsedYaml).isEqualTo(expected)
    }
}