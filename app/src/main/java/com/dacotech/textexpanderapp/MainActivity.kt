package com.dacotech.textexpanderapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var directoryUri: Uri? = null
    private lateinit var fileListView: ListView
    private lateinit var triggerListView: ListView
    private lateinit var searchView: SearchView
    private val fileList = mutableListOf<String>()
    private val triggerList = mutableListOf<String>()
    private lateinit var triggerAdapter: CustomTriggerAdapter

    private val openDocumentTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                directoryUri = uri
                CoroutineScope(Dispatchers.Main).launch {
                    readFilesFromDirectory(uri)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error accessing directory: ${e.message}", e)
                Toast.makeText(this, "Error accessing directory: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Directory selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileListView = findViewById(R.id.fileListView)
        triggerListView = findViewById(R.id.triggerListView)
        searchView = findViewById(R.id.searchView)

        triggerAdapter = CustomTriggerAdapter(this, android.R.layout.simple_list_item_1, triggerList)
        triggerListView.adapter = triggerAdapter

        selectDirectory()
        setupSearchView()
        setupTriggerListView()
    }

    private fun selectDirectory() {
        openDocumentTree.launch(null)
    }

    private suspend fun readFilesFromDirectory(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val documentTree = DocumentFile.fromTreeUri(this@MainActivity, uri) ?: throw Exception("Failed to get DocumentFile from URI")
            val matchDir = documentTree.findFile("match") ?: throw Exception("Match directory not found")
            if (matchDir.isDirectory) {
                val files = matchDir.listFiles().filter { it.name?.endsWith(".yml") == true }
                fileList.clear()
                TriggerRepository.triggers.clear()
                for (file in files) {
                    fileList.add(file.name ?: "Unknown")
                    Log.d("File Access", "Found file: ${file.name}")
                    readFileContent(file.uri)
                }
                withContext(Dispatchers.Main) {
                    updateFileListView()
                    updateTriggerListView()
                }
            } else {
                throw Exception("Match directory is not a directory")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error reading files from directory: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Error reading files from directory: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun readFileContent(uri: Uri) = withContext(Dispatchers.IO) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val text = inputStream.bufferedReader().use { it.readText() }
                TriggerRepository.loadTriggersFromYAML(text)
                Log.d("File Content", "Content of ${uri.lastPathSegment}: $text")
            } ?: throw Exception("Failed to open input stream for URI")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error reading file content: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Error parsing file: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFileListView() {
        val fileAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
        fileListView.adapter = fileAdapter
    }

    private fun updateTriggerListView() {
        triggerList.clear()
        TriggerRepository.triggers.forEach { triggerList.add("${it.trigger} -> ${it.replace}") }
        triggerAdapter.notifyDataSetChanged()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                triggerAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun setupTriggerListView() {
        triggerListView.setOnItemClickListener { _, _, position, _ ->
            val item = triggerAdapter.getItem(position)
            item?.let {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("trigger", it.split(" -> ")[1])
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Copied to clipboard: ${it.split(" -> ")[1]}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
