package com.dacotech.textexpanderapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TextProcessingUtils {
    fun replaceVariable(text: String, variable: Variable): String {
        return when (variable.type) {
            "date" -> {
                val dateFormat = variable.params?.get("format")
                if (dateFormat != null) {
                    val javaFormat = translateChronoFormatToJava(dateFormat)
                    val dateFormatter = SimpleDateFormat(javaFormat, Locale.getDefault())
                    val dateString = dateFormatter.format(Date())
                    text.replace("{{${variable.name}}}", dateString)
                } else {
                    text // Return original text if format is missing
                }
            }
            else -> text  // Handle other types as necessary
        }
    }

    private fun translateChronoFormatToJava(chronoFormat: String): String {
        val tokenMapping = mapOf(
            "%Y" to "yyyy", // Year with century
            "%C" to "yy",   // Year divided by 100 and truncated to integer (00-99)
            "%y" to "yy",   // Year without century (00-99)
            "%m" to "MM",   // Month as a decimal number (01-12)
            "%B" to "MMMM", // Full month name
            "%b" to "MMM",  // Abbreviated month name
            "%d" to "dd",   // Day of the month as a decimal number (01-31)
            "%e" to "d",    // Day of the month as a decimal number (1-31); single digit might be preceded by a space
            "%A" to "EEEE", // Full weekday name
            "%a" to "EEE",  // Abbreviated weekday name
            "%H" to "HH",   // Hour (00-23)
            "%I" to "hh",   // Hour (01-12)
            "%p" to "a",    // AM/PM designation
            "%M" to "mm",   // Minute (00-59)
            "%S" to "ss"    // Second (00-59)
            // Add more mappings as needed
        )
        return tokenMapping.entries.fold(chronoFormat) { acc, (key, value) ->
            acc.replace(key, value)
        }
    }
}