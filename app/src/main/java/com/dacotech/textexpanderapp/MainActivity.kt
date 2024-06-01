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
import android.util.Log

import android.provider.Settings
import android.net.Uri
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.list_view)

        Log.d("MainActivity", "Requesting permissions")
        checkPermissions()

        Log.d("MainActivity", "Loading triggers")
        TriggerRepository.triggers = loadTriggers()

        if (TriggerRepository.triggers.isEmpty()) {
            Log.d("MainActivity", "No triggers found")
        } else {
            Log.d("MainActivity", "Triggers loaded: ${TriggerRepository.triggers.size}")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, TriggerRepository.triggers.map { it.trigger + " -> " + it.replace })
        listView.adapter = adapter
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showStoragePermissionExplanation()
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE)
            }
        } else {
            // Permission has already been granted
            Log.d("MainActivity", "Permission already granted")
            loadTriggers()
        }
    }

    private fun showStoragePermissionExplanation() {
        // Implement a dialog to show explanation about why the permission is needed
        AlertDialog.Builder(this)
            .setTitle("Storage Permission Needed")
            .setMessage("This app needs the storage permission to read configuration files for text expansion.")
            .setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Storage permission granted")
                    loadTriggers()
                } else {
                    Log.d("MainActivity", "Storage permission denied")
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        showManualPermissionSettingDialog()
                    }
                }
            }
        }
    }

    private fun showManualPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Please go to settings and allow storage permission to use this feature.")
            .setPositiveButton("Settings") { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            .create()
            .show()
    }




    private fun loadTriggers(): List<TriggerRepository.Match> {
        val matches = mutableListOf<TriggerRepository.Match>()
        val yaml = Yaml()
        File("/storage/espanso/match/").walk().forEach { file ->
            if (file.isFile && file.extension == "yml") {
                Log.d("MainActivity", "Processing file: ${file.name}")
                val data = yaml.load<Map<String, Any>>(file.readText())
                val matchList = data["matches"] as List<Map<String, String>>
                matchList.forEach {
                    matches.add(TriggerRepository.Match(it["trigger"]!!, it["replace"]!!))
                }
            }
        }
        return matches
    }
    companion object {
        const val REQUEST_EXTERNAL_STORAGE = 1
    }
}

