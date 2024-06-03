package com.dacotech.textexpanderapp

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TriggerRepositoryTest {

    @Before
    fun setUp() {
        // Clear the repository before each test
        TriggerRepository.triggers.clear()
    }

    @Test
    fun testMultipleTriggersAreParsedCorrectly() {
        val yamlContent = """
            matches:
              - triggers: ["hello", "hi"]
                replace: "world"
        """.trimIndent()

        TriggerRepository.loadTriggersFromYAML(yamlContent)
        assertEquals(2, TriggerRepository.triggers.size)
        assertTrue(TriggerRepository.triggers.any { it.trigger == "hello" && it.replace == "world" })
        assertTrue(TriggerRepository.triggers.any { it.trigger == "hi" && it.replace == "world" })
    }

    @Test
    fun testSingleTriggerIsParsedCorrectly() {
        val yamlContent = """
            matches:
              - trigger: ":rich"
                replace: "This is a rich text."
        """.trimIndent()

        TriggerRepository.loadTriggersFromYAML(yamlContent)
        assertEquals(1, TriggerRepository.triggers.size)
        assertEquals(":rich", TriggerRepository.triggers[0].trigger)
        assertEquals("This is a rich text.", TriggerRepository.triggers[0].replace)
    }
}
