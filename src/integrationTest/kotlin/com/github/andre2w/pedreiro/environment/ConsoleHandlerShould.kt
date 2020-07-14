package com.github.andre2w.pedreiro.environment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.System.lineSeparator

class ConsoleHandlerShould {

    @Test
    internal fun `print text to console with new line`() {
        val out = System.out
        val byteArrayOutputStream = ByteArrayOutputStream()
        val text = "text to be printed"
        System.setOut(PrintStream(byteArrayOutputStream))

        val consoleHandler = ConsoleHandler()
        consoleHandler.print(text)

        assertThat("$text${lineSeparator()}").isEqualTo(String(byteArrayOutputStream.toByteArray()))
        System.setOut(out)
    }
}