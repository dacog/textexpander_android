package com.dacotech.textexpanderapp

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class TextExpanderService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val source = event.source ?: return
            handleTextChanged(source)
        }
    }

    private fun handleTextChanged(node: AccessibilityNodeInfo) {
        val text = node.text?.toString() ?: return

        // Iterate through triggers and check for matches
        TriggerRepository.triggers.forEach { match ->
            if (text.endsWith(match.trigger)) {
                val newText = text.replace(match.trigger, match.replace)
                Log.d("TextExpander", "Expanding trigger: ${match.trigger} to ${match.replace}")
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                    putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        newText
                    )
                })
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruption (e.g., log the event)
    }
}
