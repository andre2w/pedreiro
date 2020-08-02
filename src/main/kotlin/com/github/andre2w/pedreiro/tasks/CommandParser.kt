package com.github.andre2w.pedreiro.tasks

import javax.inject.Singleton

@Singleton
class CommandParser {

    private val delimiters = listOf('\'', '"')

    fun parse(command: String): ArrayList<String> {

        val args = ArrayList<String>()

        var currentWord = ""
        var inString = false
        var startingQuote = ' '
        var isEscapedChar = false

        for (character in command) {
            when {
                spaceOutsideString(character, inString) -> {
                    args.add(currentWord)
                    currentWord = ""
                }
                isEscapingCharacter(character, isEscapedChar) -> {
                    isEscapedChar = true
                }
                isQuotation(character, isEscapedChar, startingQuote) -> {
                    inString = !inString
                    startingQuote = startingQuoteFrom(startingQuote, character)
                }
                else -> {
                    isEscapedChar = false
                    currentWord += character
                }
            }
        }

        args.add(currentWord)

        return args
    }

    private fun isEscapingCharacter(character: Char, isEscapedChar: Boolean) =
            character.isBackslash() && !isEscapedChar

    private fun spaceOutsideString(character: Char, inString: Boolean) = character == ' ' && !inString

    private fun startingQuoteFrom(startingQuote: Char, character: Char): Char =
            if (startingQuote != ' ') {
                ' '
            } else {
                character
            }

    private fun isQuotation(character: Char, isEscapedChar: Boolean, startingQuote: Char) =
            character in delimiters && !isEscapedChar && characterMatchesStartingQuote(character, startingQuote)

    private fun characterMatchesStartingQuote(character: Char, startingQuote: Char) =
            character == startingQuote || startingQuote == ' '

    private fun Char.isBackslash(): Boolean = this == '\\'
}