package com.dacotech.textexpanderapp

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

import android.util.Log

class TextExpanderService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            Log.d("TextExpanderService", "Text changed event received")
            val source = event.source ?: return
            replaceTextIfNeeded(source)
        }
    }

    private fun replaceTextIfNeeded(source: AccessibilityNodeInfo) {
        val text = source.text.toString()
        Log.d("TextExpanderService", "Checking text: $text")
        TriggerRepository.triggers.forEach { match ->
            if (text.endsWith(match.trigger)) {
                Log.d("TextExpanderService", "Match found: ${match.trigger}")
                val newText = text.replace(match.trigger, match.replace)
                source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText)
                })
                Log.d("TextExpanderService", "Text replaced with: $newText")
            }
        }
    }

    override fun onInterrupt() {
        Log.d("TextExpanderService", "Service interrupted")
    }
}