package com.github.andre2w.pedreiro.blueprints.loaders

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.blueprints.Blueprint

interface BlueprintLoader {
    fun loadFrom(path: String, arguments: Arguments) : Blueprint
}