package com.example.plantdoctor  // or adjust to your structure

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imagePath: String,
    val prediction: String,
    val confidence: String,
    val timestamp: Long
)
