package com.dacotech.textexpanderapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile

class MainActivity : AppCompatActivity() {
    private var directoryUri: Uri? = null
    private lateinit var listView: ListView
    private val fileList = mutableListOf<String>()

    // Register a contract to open a document tree, used for selecting the directory
    private val openDocumentTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            directoryUri = uri
            readFilesFromDirectory(uri)
        } else {
            Toast.makeText(this, "Directory selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        selectDirectory()
    }

    private fun selectDirectory() {
        openDocumentTree.launch(null)
    }

    private fun readFilesFromDirectory(uri: Uri) {
        val documentTree = DocumentFile.fromTreeUri(this, uri) ?: return
        val matchDir = documentTree.findFile("match")
        if (matchDir != null && matchDir.isDirectory) {
            val files = matchDir.listFiles().filter { it.name?.endsWith(".yml") == true }
            fileList.clear()
            for (file in files) {
                fileList.add(file.name ?: "Unknown")
                Log.d("File Access", "Found file: ${file.name}")
            }
            updateListView()
        } else {
            Toast.makeText(this, "Match directory not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
        listView.adapter = adapter
    }

    private fun readFileContent(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val text = inputStream.bufferedReader().use { it.readText() }
            Log.d("File Content", "Content of ${uri.lastPathSegment}: $text")
            // Parse the YAML content to extract triggers and values
        }
    }
}
