package com.github.andre2w.pedreiro

data class Arguments(val blueprintName: String, val variables: Map<String, String>, val verbose: Boolean = false) {

    constructor(blueprintName: String) : this(blueprintName, emptyMap())

    fun mergeWith(argsToMerge: Map<String, String>): Arguments {
        val mergedArguments = HashMap<String, String>()

        argsToMerge.forEach { arg -> mergedArguments[arg.key] = arg.value }
        variables.forEach { arg -> mergedArguments[arg.key] = arg.value }

        return Arguments(blueprintName, mergedArguments)
    }
}
