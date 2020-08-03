package com.github.andre2w.pedreiro.yaml

interface YamlNodeNew {

    companion object {
        fun parse(node: Any) : YamlNodeNew{
            return when (node) {
                is Map<*, *> -> YamlObject(node as Map<String, Any>)
                is List<*> -> buildYamlList(node)
                else -> TODO()
            }
        }

        private fun buildYamlList(nodes: Any) : YamlList {
            val parsedNodes = (nodes as List<*>).map { node -> parse(node as Any) }
            return YamlList(parsedNodes)
        }

    }

}