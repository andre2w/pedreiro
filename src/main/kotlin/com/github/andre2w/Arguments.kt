package com.github.andre2w

data class Arguments(val blueprintName: String, val extraArguments: Map<String, String>) {

    fun mergeWith(argsToMerge: Map<String,String>): Arguments {
        val mergedArguments = HashMap<String, String>()

        argsToMerge.forEach { arg -> mergedArguments[arg.key] = arg.value }
        extraArguments.forEach { arg -> mergedArguments[arg.key] = arg.value }

        return Arguments(blueprintName, mergedArguments)
    }
}