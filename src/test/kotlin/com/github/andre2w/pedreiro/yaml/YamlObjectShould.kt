package com.github.andre2w.pedreiro.yaml

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class YamlObjectShould {

    @Test
    fun `return value as string`() {
        val yamlObject = YamlObject(mapOf(
                "field" to "text"
        ))

        assertThat(yamlObject.stringFrom("field")).isEqualTo("text")
    }

    @Test
    internal fun `throw exception when field doesnt exist`() {
        val yamlObject = YamlObject(mapOf(
                "field" to "text"
        ))

        assertThrows<FieldNotFound> {
            yamlObject.stringFrom("invalid_field")
        }
    }


}