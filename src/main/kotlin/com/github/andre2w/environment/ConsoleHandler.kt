package com.github.andre2w.environment

import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class ConsoleHandler {

    fun print(text: String) {
        println(text)
    }

    fun exitWith(code: Int) {
        exitProcess(code)
    }

}