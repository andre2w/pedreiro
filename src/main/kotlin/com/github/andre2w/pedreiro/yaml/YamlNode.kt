package com.github.andre2w.pedreiro.yaml

class YamlNode(private val node: Any) : Iterable<YamlNode> {

    fun isArray(): Boolean {
        return node is List<*>
    }

    fun getTextFromField(fieldName: String): String {
        return (node as Map<*, *>)[fieldName] as String!!
    }

    fun getChildren(fieldName: String): YamlNode? {
        return (node as Map<*, *>)[fieldName]?.let { YamlNode(it) }
    }

    fun hasField(fieldName: String): Boolean {
        return (node as Map<*, *>).containsKey(fieldName)
    }

    override fun iterator(): Iterator<YamlNode> {
        return (node as List<*>)
                .map { YamlNode(it as Any) }
                .iterator()
    }

}