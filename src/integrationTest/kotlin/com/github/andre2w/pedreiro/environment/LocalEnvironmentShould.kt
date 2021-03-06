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

    @Test
    fun `know user operating system`() {
        val os = System.getProperty("os.name")

        val environment = LocalEnvironment()

        assertThat(environment.osName()).isEqualTo(os)
    }

    @Test
    internal fun `be able to retrieve environment variables`() {
        val variable = "JAVA_HOME"
        val expectedValue = System.getenv(variable)

        val environment = LocalEnvironment()

        assertThat(environment.variable(variable)).isEqualTo(expectedValue)
    }
}
