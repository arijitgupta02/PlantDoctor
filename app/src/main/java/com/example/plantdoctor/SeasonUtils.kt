package com.example.plantdoctor

import java.util.Calendar

object SeasonUtils {

    fun getCurrentSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        return when (month) {
            in 5..8 -> "Monsoon"    // June to September
            in 9..11 -> "Autumn"    // October to December
            in 0..1 -> "Winter"     // January to February
            in 2..4 -> "Summer"     // March to May
            else -> "Unknown"
        }
    }

    fun getSeasonalTip(): String {
        return when (getCurrentSeason()) {
            "Monsoon" -> "🌧️ Monsoon Alert: Fungal diseases are common. Keep leaves dry!"
            "Summer" -> "☀️ Summer Tip: Water plants in the early morning or evening."
            "Winter" -> "❄️ Winter Caution: Reduce watering; plants grow slower."
            "Autumn" -> "🍂 Autumn Tip: Prune dead leaves and prepare for winter."
            else -> "🌱 Tip: Take care of your plants with love every season!"
        }
    }
}