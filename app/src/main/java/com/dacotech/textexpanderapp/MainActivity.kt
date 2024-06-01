package com.dacotech.textexpanderapp

import com.dacotech.textexpanderapp.TriggerRepository.Match


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.yaml.snakeyaml.Yaml
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.list_view)
        checkPermissions()

        TriggerRepository.triggers = loadTriggers()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, TriggerRepository.triggers.map { it.trigger + " -> " + it.replace })
        listView.adapter = adapter
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

    private fun loadTriggers(): List<TriggerRepository.Match> {
        val matches = mutableListOf<TriggerRepository.Match>() // Ensure this uses the centralized Match
        val yaml = Yaml()
        File("/storage/emulated/espanso/match/").walk().forEach { file ->
            if (file.isFile && file.extension == "yml") {
                val data = yaml.load<Map<String, Any>>(file.readText())
                val matchList = data["matches"] as List<Map<String, String>>
                matchList.forEach {
                    matches.add(TriggerRepository.Match(it["trigger"]!!, it["replace"]!!))
                }
            }
        }
        return matches
    }

    data class Match(val trigger: String, val replace: String)
}
