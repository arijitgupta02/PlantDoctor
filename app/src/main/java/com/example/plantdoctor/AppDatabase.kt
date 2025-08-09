package com.example.plantdoctor

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plantdoctor.ScanHistoryItem

@Database(entities = [ScanHistoryItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plant_doctor_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
