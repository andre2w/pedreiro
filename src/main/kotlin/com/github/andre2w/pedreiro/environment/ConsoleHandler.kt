package com.github.andre2w.pedreiro.environment

import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class ConsoleHandler {

    private var debug: Boolean = false

    fun print(text: String) {
        println(text)
    }

    fun exitWith(code: Int) {
        exitProcess(code)
    }

    fun printError(text: String) {
        System.err.println(text)
    }

    fun activeDebugMode() {
        debug = true
    }

    fun printDebug(text: String) {
        if (debug) print(text)
    }

}