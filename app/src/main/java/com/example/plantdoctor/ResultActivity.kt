package com.example.plantdoctor

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultImageView: ImageView = findViewById(R.id.resultImageView)
        val resultText: TextView = findViewById(R.id.resultText)
        val backButton: Button = findViewById(R.id.backButton)

        val imagePath = intent.getStringExtra("imagePath")
        val prediction = intent.getStringExtra("prediction")

        if (!imagePath.isNullOrEmpty()) {
            val imgFile = File(imagePath)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                resultImageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Image file not found.", Toast.LENGTH_SHORT).show()
            }
        }

        resultText.text = prediction ?: "No prediction available"

        backButton.setOnClickListener {
            finish()
        }
    }
}
