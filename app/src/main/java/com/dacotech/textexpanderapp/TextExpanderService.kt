package com.dacotech.textexpanderapp

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import java.util.Locale
import java.util.Date
import java.text.SimpleDateFormat

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
                Log.i("TextExpanderService", "I found trigger ${match.trigger}")
                var newText = match.replaceText
                match.variables?.forEach { variable ->
                    newText = TextProcessingUtils.replaceVariable(newText, variable)
                }
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, newText)
                })
            }
        }
    }

    override fun onInterrupt() {
        Log.d("TextExpanderService", "Service has been interrupted")
    }
}
