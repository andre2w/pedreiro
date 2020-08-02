package com.github.andre2w.pedreiro

data class Arguments(val blueprintName: String, val extraArguments: Map<String, String>, val verbose: Boolean = false) {

    constructor(blueprintName: String) : this(blueprintName, emptyMap())

    fun mergeWith(argsToMerge: Map<String, String>): Arguments {
        val mergedArguments = HashMap<String, String>()

        argsToMerge.forEach { arg -> mergedArguments[arg.key] = arg.value }
        extraArguments.forEach { arg -> mergedArguments[arg.key] = arg.value }

        return Arguments(blueprintName, mergedArguments)
    }
}