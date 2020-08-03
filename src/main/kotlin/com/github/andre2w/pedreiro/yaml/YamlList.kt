package com.github.andre2w.pedreiro.yaml

class YamlList(
        private val nodes: List<YamlNodeNew>
) : YamlNodeNew, Iterable<YamlNodeNew> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun iterator(): Iterator<YamlNodeNew> {
        return nodes.iterator()
    }

}
