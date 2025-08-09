package com.example.plantdoctor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plantdoctor.databinding.ActivityHistoryBinding
import android.view.View
import android.content.Intent
import java.io.File
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantdoctor.ScanHistoryAdapter
import com.example.plantdoctor.AppDatabase
import kotlinx.coroutines.*
import com.example.plantdoctor.ScanHistoryItem


class HistoryActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 101
    private lateinit var binding: ActivityHistoryBinding
    private var imageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToCameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                launchCamera()
            }
        }
        binding.clearHistoryButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getDatabase(this@HistoryActivity).scanHistoryDao().deleteAll()

                withContext(Dispatchers.Main) {
                    loadHistory()
                    Toast.makeText(this@HistoryActivity, "History cleared", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Set up RecyclerView layout manager and adapter once
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = ScanHistoryAdapter(this, emptyList())

        // 🔁 Initial load
        loadHistory()
    }

    override fun onResume() {
        super.onResume()
        // 🔁 Reload when returning to this screen
        loadHistory()
    }

    private fun loadHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@HistoryActivity)
            val historyList = db.scanHistoryDao().getAllHistory()

            withContext(Dispatchers.Main) {
                val adapter = binding.historyRecyclerView.adapter as ScanHistoryAdapter
                if (historyList.isEmpty()) {
                    binding.emptyCard.visibility = View.VISIBLE
                    binding.historyRecyclerView.visibility = View.GONE
                    binding.goToCameraButton.visibility = View.VISIBLE
                    binding.clearHistoryButton.visibility = View.GONE
                } else {
                    adapter.updateData(historyList)
                    binding.emptyCard.visibility = View.GONE
                    binding.historyRecyclerView.visibility = View.VISIBLE
                    binding.goToCameraButton.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.VISIBLE
                }
            }
        }
    }


    private val imageCaptureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageFile != null) {
            val imageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                imageFile!!
            )
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("imageUri", imageUri.toString())
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun launchCamera() {
        imageFile = File(cacheDir, "captured_image.jpg")
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile!!)
        imageCaptureLauncher.launch(uri)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }



    // We'll populate data here after Room is set up
}
