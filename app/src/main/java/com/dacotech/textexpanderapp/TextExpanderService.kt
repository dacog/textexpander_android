package com.dacotech.textexpanderapp

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class TextExpanderService : AccessibilityService() {

    private val config: MutableMap<String, Any> = mutableMapOf()

    override fun onServiceConnected() {
        super.onServiceConnected()
        loadConfigFromExternalStorage()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val source = event.source ?: return
            val text = source.text?.toString() ?: return
            val expandedText = expandText(text)
            if (expandedText != text) {
                source.performAction(
                    AccessibilityNodeInfo.ACTION_SET_TEXT,
                    Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, expandedText)
                    }
                )
            }
        }
    }

    override fun onInterrupt() {}

    private fun loadConfigFromExternalStorage() {
//        val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath
        val externalStoragePath = "/storage"
        val configDir = File("$externalStoragePath/espanso/config")

        if (configDir.exists() && configDir.isDirectory) {
            configDir.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "yml") {
                    val inputStream = FileInputStream(file)
                    loadConfig(inputStream)
                } else if (file.isDirectory && file.name == "match") {
                    file.listFiles()?.forEach { matchFile ->
                        if (matchFile.isFile && matchFile.extension == "yml") {
                            val inputStream = FileInputStream(matchFile)
                            loadConfig(inputStream)
                        }
                    }
                }
            }
        } else {
            Log.e("TextExpanderService", "Config directory does not exist: $configDir")
        }
    }

    private fun loadConfig(inputStream: InputStream) {
        val yaml = Yaml()
        val loadedConfig: Map<String, Any> = yaml.load(inputStream) as Map<String, Any>
        mergeConfig(loadedConfig)
    }

    private fun mergeConfig(loadedConfig: Map<String, Any>) {
        loadedConfig.forEach { (key, value) ->
            if (key == "matches" && value is List<*>) {
                val existingMatches = config.getOrPut(key) { mutableListOf<Map<String, String>>() } as MutableList<Map<String, String>>
                value.filterIsInstance<Map<String, String>>().forEach { match ->
                    existingMatches.add(match)
                }
            } else {
                config[key] = value
            }
        }
    }

    private fun expandText(text: String): String {
        var expandedText = text
        val matches = config["matches"] as? List<Map<String, String>> ?: emptyList()
        for (match in matches) {
            val trigger = match["trigger"] ?: continue
            val replace = match["replace"] ?: continue
            expandedText = expandedText.replace(trigger, replace)
        }
        return expandedText
    }
}
