package com.github.andre2w.matchers

import io.mockk.Matcher
import io.mockk.MockKMatcherScope

data class VerifyIgnoringLineEnding<T>(
        val expectedText: String
) : Matcher<T> {

    override fun match(arg: T?): Boolean {
        val expected = expectedText.lines()
        val actual = arg.toString().lines()
        val result = expected.zip(actual).fold(true) { _, pair -> pair.first == pair.second }
        return result
    }
}

inline fun <reified T : String> MockKMatcherScope.ignoringLineEnding(
        text: String
): T = match(VerifyIgnoringLineEnding(text))