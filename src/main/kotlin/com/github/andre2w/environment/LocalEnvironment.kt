package com.github.andre2w.environment

import javax.inject.Singleton

@Singleton
class LocalEnvironment {

    fun currentDir(): String = System.getProperty("user.dir")

    fun userHome(): String = System.getProperty("user.home")

}
