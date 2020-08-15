package com.github.andre2w.pedreiro.yaml

import kotlin.collections.List as KotlinList

sealed class YamlNode {

    companion object {
        internal fun parse(node: Any): YamlNode {
            return when (node) {
                is Map<*, *> -> Object(node)
                is KotlinList<*> -> buildYamlList(node)
                else -> Value(node)
            }
        }

        private fun buildYamlList(nodes: Any): List {
            val parsedNodes = (nodes as KotlinList<*>).map { node -> parse(node as Any) }
            return List(parsedNodes)
        }
    }

    data class Value(private val value: Any) : YamlNode() {

        fun asText(): String = value.toString()

        fun asInt(): Int = value as Int
    }

    object Missing : YamlNode()

    data class Object(private val keyValues: Map<*, *>) : YamlNode() {

        operator fun get(field: String): YamlNode {
            return keyValues[field]?.let { parse(it) } ?: Missing
        }
    }

    data class List(private val nodes: KotlinList<YamlNode>) : YamlNode(), KotlinList<YamlNode> {

        override fun iterator(): Iterator<YamlNode> {
            return nodes.iterator()
        }

        override val size: Int
            get() = nodes.size

        override fun contains(element: YamlNode): Boolean {
            return nodes.contains(element)
        }

        override fun containsAll(elements: Collection<YamlNode>): Boolean {
            return nodes.containsAll(elements)
        }

        override fun get(index: Int): YamlNode {
            return nodes[index]
        }

        override fun indexOf(element: YamlNode): Int {
            return nodes.indexOf(element)
        }

        override fun isEmpty(): Boolean {
            return nodes.isEmpty()
        }

        override fun lastIndexOf(element: YamlNode): Int {
            return nodes.lastIndexOf(element)
        }

        override fun listIterator(): ListIterator<YamlNode> {
            return nodes.listIterator()
        }

        override fun listIterator(index: Int): ListIterator<YamlNode> {
            return nodes.listIterator(index)
        }

        override fun subList(fromIndex: Int, toIndex: Int): KotlinList<YamlNode> {
            return nodes.subList(fromIndex, toIndex)
        }
    }
}
