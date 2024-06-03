package com.dacotech.textexpanderapp

object TriggerRepository {
    val triggers = mutableListOf<Match>()

    data class Match(val trigger: String, val replace: String)

    fun loadTriggersFromYAML(yamlContent: String) {
        val yaml = org.yaml.snakeyaml.Yaml()
        val data = yaml.load<Map<String, Any>>(yamlContent)
        val matches = data["matches"] as List<Map<String, Any>>
        matches.forEach { match ->
            val replaceText = match["replace"] as? String ?: ""
            if ("triggers" in match) {
                (match["triggers"] as List<String>).forEach { trigger ->
                    triggers.add(Match(trigger, replaceText))
                }
            } else if ("trigger" in match) {
                triggers.add(Match(match["trigger"] as String, replaceText))
            }
        }
    }
}
