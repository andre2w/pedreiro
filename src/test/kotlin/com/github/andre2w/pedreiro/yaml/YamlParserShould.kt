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

        val parsedYaml = yamlParser.parse(yaml)

        val expected = YamlObject(mapOf(
                "field" to "test"
        ))
        assertThat(parsedYaml).isEqualTo(expected)
    }
}