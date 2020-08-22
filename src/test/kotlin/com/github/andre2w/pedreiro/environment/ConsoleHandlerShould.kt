package com.github.andre2w.pedreiro.environment

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ConsoleHandlerShould {

    @ParameterizedTest
    @CsvSource(
            "WINDOWS,WINDOWS",
            "Windows 10,WINDOWS",
            "Linux x86, LINUX",
            "windows, WINDOWS",
            "Mac OS X, MAC_OS",
            "mac os x, MAC_OS"
    )
    internal fun `parse OS name into Platform`(osName : String, platform: Platform) {
        val environment = mockk<LocalEnvironment>()
        val consoleHandler = ConsoleHandler(environment)
        every { environment.osName() } returns osName

        assertThat(consoleHandler.currentPlatform()).isEqualTo(platform)
    }
}