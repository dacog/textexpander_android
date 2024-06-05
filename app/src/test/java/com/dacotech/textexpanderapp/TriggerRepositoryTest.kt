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
        assertTrue(TriggerRepository.triggers.any { it.trigger == "hello" && it.replaceText == "world" })
        assertTrue(TriggerRepository.triggers.any { it.trigger == "hi" && it.replaceText == "world" })
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
        assertEquals("This is a rich text.", TriggerRepository.triggers[0].replaceText)
    }

    @Test
    fun testVariablesWithTriggersAreParsedCorrectly() {
        val yamlContent = """
            matches:
              - trigger: ":date"
                replace: "The current date is: {{date}}"
                vars:
                  - name: "date"
                    type: "date"
                    params:
                      format: "%Y-%m-%d"
        """.trimIndent()

        TriggerRepository.loadTriggersFromYAML(yamlContent)
        assertEquals(1, TriggerRepository.triggers.size)
        assertEquals(":date", TriggerRepository.triggers[0].trigger)
        assertEquals("The current date is: {{date}}", TriggerRepository.triggers[0].replaceText)
        assertNotNull(TriggerRepository.triggers[0].variables)
        assertEquals(1, TriggerRepository.triggers[0].variables?.size)
        assertEquals("date", TriggerRepository.triggers[0].variables?.get(0)?.name)
        assertEquals("date", TriggerRepository.triggers[0].variables?.get(0)?.type)
        assertEquals("%Y-%m-%d", TriggerRepository.triggers[0].variables?.get(0)?.params?.get("format"))
    }
}
