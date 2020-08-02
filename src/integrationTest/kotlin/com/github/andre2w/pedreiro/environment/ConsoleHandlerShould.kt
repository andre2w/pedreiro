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

    @Test
    internal fun `print text to stderr`() {
        val out = System.err
        val byteArrayOutputStream = ByteArrayOutputStream()
        val text = "text to be printed"
        System.setErr(PrintStream(byteArrayOutputStream))

        val consoleHandler = ConsoleHandler()
        consoleHandler.printError(text)

        assertThat("$text${lineSeparator()}").isEqualTo(String(byteArrayOutputStream.toByteArray()))
        System.setErr(out)
    }

    @Test
    internal fun `print debug text only when debug mode is activated`() {
        val out = System.out
        val byteArrayOutputStream = ByteArrayOutputStream()
        val hiddenText = "this should not be printed"
        val printedText = "text to be printed"
        System.setOut(PrintStream(byteArrayOutputStream))

        val consoleHandler = ConsoleHandler()
        consoleHandler.printDebug(hiddenText)
        consoleHandler.activeDebugMode()
        consoleHandler.printDebug(printedText)

        assertThat("$printedText${lineSeparator()}").isEqualTo(String(byteArrayOutputStream.toByteArray()))
        System.setOut(out)
    }
}