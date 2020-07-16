package com.github.andre2w.matchers

import io.mockk.Matcher
import io.mockk.MockKMatcherScope

data class VerifyIgnoringLineEnding<T>(
        val expectedText: String
) : Matcher<T> {

    override fun match(arg: T?): Boolean {
        val expected = expectedText.lines()
        val actual = arg.toString().lines()
        return compareStrings(expected, actual)
    }

    private fun compareStrings(expected: List<String>, actual: List<String>) =
            expected.zip(actual).fold(true) { result, pair ->
                pair.first == pair.second && result
            }
}

inline fun <reified T : String> MockKMatcherScope.ignoringLineEnding(
        text: String
): T = match(VerifyIgnoringLineEnding(text))