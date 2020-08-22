package com.github.andre2w.pedreiro.environment

import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class ConsoleHandler(private val environment: LocalEnvironment) {

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

    fun currentPlatform(): Platform {
        val osName = environment.osName()
        return when {
            osName.contains("windows", ignoreCase = true) -> Platform.WINDOWS
            osName.contains("mac", ignoreCase = true) -> Platform.MAC_OS
            osName.contains("linux", ignoreCase = true) -> Platform.LINUX
            else -> Platform.UNSUPPORTED
        }
    }
}
