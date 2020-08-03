package com.github.andre2w.pedreiro.yaml

class YamlObject(
        private val keyValues: Map<String, Any>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YamlObject

        if (keyValues != other.keyValues) return false

        return true
    }

    override fun hashCode(): Int {
        return keyValues.hashCode()
    }

    override fun toString(): String {
        return "YamlObject(keyValues=$keyValues)"
    }

    fun stringFrom(field: String): String {
        return keyValues[field]?.let { value -> value.toString() } ?: throw FieldNotFound()
    }
}
