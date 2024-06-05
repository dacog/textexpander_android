package com.dacotech.textexpanderapp

import org.yaml.snakeyaml.Yaml
import android.util.Log

data class Variable(
    val name: String,
    val type: String,
    val params: Map<String, String>?
)

data class Match(
    val trigger: String,
    val replaceText: String,
    val variables: List<Variable>?
)

object TriggerRepository {
    var triggers: MutableList<Match> = mutableListOf()

    fun loadTriggersFromYAML(yamlContent: String) {
        try {
            val yaml = Yaml()
            val data = yaml.load<Map<String, Any>>(yamlContent)
            val matchesList = data["matches"] as? List<Map<String, Any>> ?: emptyList()

            matchesList.forEach { matchMap ->
                val replaceText = matchMap["replace"] as? String ?: ""
                val vars = (matchMap["vars"] as? List<Map<String, Any>>)?.mapNotNull { parseVariable(it) } ?: emptyList()
                // Check if "triggers" key is present and is a List of Strings
                if (matchMap.containsKey("triggers") && matchMap["triggers"] is List<*>) {
                    val multipleTriggers = matchMap["triggers"] as List<String>
                    multipleTriggers.forEach { trigger ->
                        triggers.add(Match(trigger, replaceText, vars))
                    }
                } else if (matchMap.containsKey("trigger") && matchMap["trigger"] is String) {
                    // Single trigger case
                    val trigger = matchMap["trigger"] as? String ?: ""
                    triggers.add(Match(trigger, replaceText, vars))
                }
            }
        } catch (e: Exception) {
            Log.e("TriggerRepository", "Error parsing YAML: ${e.message}")
        }
    }

    private fun parseVariable(data: Map<String, Any>): Variable? {
        val name = data["name"] as? String ?: return null
        val type = data["type"] as? String ?: return null
        val params = data["params"] as? Map<String, String>

        return Variable(name, type, params)
    }
}
