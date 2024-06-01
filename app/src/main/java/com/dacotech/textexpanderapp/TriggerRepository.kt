package com.dacotech.textexpanderapp

object TriggerRepository {
    val triggers = mutableListOf<Match>()

    data class Match(val trigger: String, val replace: String)

    fun loadTriggersFromYAML(yamlContent: String) {
        val yaml = org.yaml.snakeyaml.Yaml()
        val data = yaml.load<Map<String, Any>>(yamlContent)
        val matches = data["matches"] as List<Map<String, String>>
        matches.forEach {
            triggers.add(Match(it["trigger"]!!, it["replace"]!!))
        }
    }
}
