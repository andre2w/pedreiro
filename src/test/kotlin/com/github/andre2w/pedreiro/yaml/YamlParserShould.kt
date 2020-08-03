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

        val parsedYaml = yamlParser.parse(yaml) as YamlObject

        val expected = YamlObject(mapOf(
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

        val parsedYaml = yamlParser.parse(yaml) as YamlList

        val expected = YamlList(listOf(
                YamlObject(mapOf( "first_field" to "test" )),
                YamlObject(mapOf( "second_field" to "second value" ))
        ))
        assertThat(parsedYaml).isEqualTo(expected)
    }
}