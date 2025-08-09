package com.example.plantdoctor // or use your preferred structure

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.plantdoctor.ScanHistoryItem

@Dao
interface ScanHistoryDao {

    @Insert
    suspend fun insert(item: ScanHistoryItem)

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    suspend fun getAllHistory(): List<ScanHistoryItem>

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()

    @Query("DELETE FROM scan_history")
    suspend fun deleteAll()

}
