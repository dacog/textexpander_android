package com.dacotech.textexpanderapp

object TriggerRepository {
    var triggers: List<Match> = listOf()

    data class Match(val trigger: String, val replace: String)
}

