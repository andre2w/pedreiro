package com.github.andre2w.pedreiro.tasks

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommandParserShould {

    private val commandParser = CommandParser()

    @Test
    fun `parse command to list of arguments`() {
        val expectedCommand = listOf(
            "gradle",
            "init",
            "--type",
            "java-application",
            "--test-framework",
            "junit",
            "--dsl",
            "groovy",
            "--project-name",
            "test",
            "--package",
            "com.example.test"
        )
        val command = expectedCommand.joinToString(" ")

        val parsedCommand = commandParser.parse(command)

        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with multiple words string enclosed with double quote as argument`() {
        val command = "echo \"test argument parsing\""

        val parsedCommand = commandParser.parse(command)

        val expectedCommand = listOf("echo", "test argument parsing")
        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with multiple words string enclosed with single quote as argument`() {
        val command = "echo \'test argument parsing\'"

        val parsedCommand = commandParser.parse(command)

        val expectedCommand = listOf("echo", "test argument parsing")
        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with escaped quote`() {
        val command = "echo \'there\\'s a quote in this argument\'"

        val parsedCommand = commandParser.parse(command)

        val expectedCommand = listOf("echo", "there\'s a quote in this argument")
        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with nested quotes`() {
        val command = "echo \"this \'is a\' message\""

        val parsedCommand = commandParser.parse(command)

        val expectedCommand = listOf("echo", "this \'is a\' message")
        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with nested double quotes`() {
        val command = "echo \'this \"is a\" message\'"

        val parsedCommand = commandParser.parse(command)

        val expectedCommand = listOf("echo", "this \"is a\" message")
        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }

    @Test
    fun `parse command with windows path`() {
        val command = "C:\\\\'Program Files'\\\\gradle\\\\bin\\\\gradle --version"

        val parsedCommand = commandParser.parse(command)

        val expectedCommand = listOf("C:\\Program Files\\gradle\\bin\\gradle", "--version")
        assertThat(parsedCommand).isEqualTo(expectedCommand)
    }
}
