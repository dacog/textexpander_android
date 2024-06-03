package com.dacotech.textexpanderapp

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import java.util.Locale
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

class TextExpanderService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val source = event.source ?: return
            handleTextChanged(source)
        }
    }

    private fun handleTextChanged(node: AccessibilityNodeInfo) {
        val text = node.text?.toString() ?: return

        TriggerRepository.triggers.forEach { match ->
            if (text.endsWith(match.trigger)) {
                var newText = match.replace
                match.vars.forEach { variable ->
                    newText = replaceVariable(newText, variable)
                }
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText)
                })
            }
        }
    }

    private fun replaceVariable(text: String, variable: TriggerRepository.Variable): String {
        return when (variable.type) {
            "date" -> {
                val dateFormat = SimpleDateFormat(variable.params["format"], Locale.getDefault())
                text.replace("{{${variable.name}}}", dateFormat.format(Date()))
            }
            else -> text
        }
    }

    private fun formatDate(format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onInterrupt() {
        // Handle service interruption (e.g., log the event)
    }
}
