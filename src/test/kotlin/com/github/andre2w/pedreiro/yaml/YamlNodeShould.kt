package com.github.andre2w.pedreiro.yaml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class YamlNodeShould {

    @Test
    fun `return Missing type when node does not exists`() {
        val yamlObject = YamlNode.Object(
            mapOf(
                "field" to "text"
            )
        )

        assertThat(yamlObject["invalid_field"]).isEqualTo(YamlNode.Missing)
    }

    @Test
    fun `return text when type is Value`() {
        val yamlValue = YamlNode.Value("text")

        assertThat(yamlValue.asText()).isEqualTo("text")
    }

    @Test
    fun `return number when asked Integer to Value type`() {
        val yamlValue = YamlNode.Value(5)

        assertThat(yamlValue.asInt()).isEqualTo(5)
    }
}
