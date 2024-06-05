package com.dacotech.textexpanderapp

import org.junit.jupiter.api.Assertions.*

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals

class TextProcessingUtilsTest {
    @Test
    fun testDateReplacementWithValidFormat() {
        val text = "Today is {{date}}"
        val variable = Variable("date", "date", mapOf("format" to "%Y-%m-%d"))
        val result = TextProcessingUtils.replaceVariable(text, variable)

        val expectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val expectedText = "Today is $expectedDate"

        assertEquals(expectedText, result)
    }

    @Test
    fun testDateReplacementWithInvalidFormat() {
        val text = "Current time: {{time}}"
        val variable = Variable("time", "date", mapOf("format" to "%H:%M:%S")) // Using a non-existent format
        val result = TextProcessingUtils.replaceVariable(text, variable)

        val expectedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val expectedText = "Current time: $expectedTime"

        assertEquals(expectedText, result)
    }

    @Test
    fun testDateReplacementWithMissingFormat() {
        val text = "The time is {{time}}"
        val variable = Variable("time", "date", null) // No format provided
        val result = TextProcessingUtils.replaceVariable(text, variable)

        // Expecting the original text since no format is provided
        assertEquals("The time is {{time}}", result)
    }

    @Test
    fun testNonDateTypeVariable() {
        val text = "Hello, {{name}}"
        val variable = Variable("name", "text", mapOf("dummy" to "value"))
        val result = TextProcessingUtils.replaceVariable(text, variable)

        // Expecting the original text since it's not a date type
        assertEquals("Hello, {{name}}", result)
    }
}
