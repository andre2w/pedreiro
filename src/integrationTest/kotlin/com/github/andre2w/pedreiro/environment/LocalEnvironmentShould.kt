package com.github.andre2w.pedreiro.environment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalEnvironmentShould {

    @Test
    fun `return folder where application was called from`() {
        val currentFolder = System.getProperty("user.dir")

        val environment = LocalEnvironment()

        assertThat(environment.currentDir()).isEqualTo(currentFolder)
    }

    @Test
    fun `know where is user home folder`() {
        val homeFolder = System.getProperty("user.home")

        val environment = LocalEnvironment()

        assertThat(environment.userHome()).isEqualTo(homeFolder)
    }

}