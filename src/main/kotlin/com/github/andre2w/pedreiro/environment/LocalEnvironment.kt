package com.github.andre2w.pedreiro.environment

import javax.inject.Singleton

@Singleton
class LocalEnvironment {

    fun currentDir(): String = System.getProperty("user.dir")

    fun userHome(): String = System.getProperty("user.home")

    fun osName(): String = System.getProperty("os.name")
}
