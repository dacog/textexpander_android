package com.dacotech.textexpanderapp


object TriggerRepository {
    val triggers = mutableListOf<Match>()

    data class Match(
        val trigger: String,
        val replace: String,
        val vars: List<Variable> = listOf()
    )

    data class Variable(
        val name: String,
        val type: String,
        val params: Map<String, String>
    )


    fun loadTriggersFromYAML(yamlContent: String) {
        val yaml = org.yaml.snakeyaml.Yaml()
        val data = yaml.load<Map<String, Any>>(yamlContent)
        val matches = data["matches"] as? List<Map<String, Any>> ?: return

        matches.forEach { match ->
            val replaceText = match["replace"] as? String ?: ""

            // Check if "triggers" key is present and is a List of Strings
            if (match.containsKey("triggers") && match["triggers"] is List<*>) {
                val multipleTriggers = match["triggers"] as List<String>
                multipleTriggers.forEach { trigger ->
                    triggers.add(Match(trigger, replaceText, emptyList()))
                }
            } else if (match.containsKey("trigger") && match["trigger"] is String) {
                // Single trigger case
                val trigger = match["trigger"] as String
                triggers.add(Match(trigger, replaceText, emptyList()))
            }
        }
    }


}
