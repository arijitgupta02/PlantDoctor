package com.example.plantdoctor

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantdoctor.databinding.ActivityTipsBinding
import android.speech.tts.TextToSpeech
import java.util.*
import android.widget.ImageButton

class TipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTipsBinding
    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false
    private lateinit var currentTipsMap: Map<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔊 Initialize TTS
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show()
                } else {
                    isTtsInitialized = true
                }
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
            }
        }

        // 🌦 Weather data
        val weather = intent.getStringExtra("weather") ?: "☁️ Unknown"
        val suggestion = intent.getStringExtra("suggestion") ?: "🍃 Monitor your plant carefully"
        val city = extractCity(weather)

        binding.tipsWeatherInfo.text = weather
        binding.tipsLocation.text = city

        // 🌤 Animation
        val animation = when {
            weather.contains("rain", true) || weather.contains("drizzle", true) -> R.raw.rain
            weather.contains("clear", true) -> R.raw.sunny
            weather.contains("cloud", true) -> R.raw.cloud
            weather.contains("thunder", true) -> R.raw.storm
            else -> R.raw.cloud
        }
        binding.tipsWeatherAnimation.setAnimation(animation)
        binding.tipsWeatherAnimation.playAnimation()

        // 🌱 Load tips
        val weatherKey = when {
            weather.contains("rain", true) || weather.contains("drizzle", true) -> "rain"
            weather.contains("clear", true) -> "clear"
            weather.contains("cloud", true) -> "cloud"
            weather.contains("thunder", true) -> "thunder"
            else -> "default"
        }

        loadAndShowTips(weatherKey)

        // 🔁 Refresh button
        binding.refreshTipButton.setOnClickListener {
            val blink = AnimationUtils.loadAnimation(this, android.R.anim.fade_in).apply {
                duration = 300
                repeatMode = Animation.REVERSE
                repeatCount = 1
            }
            binding.tipsContainer.startAnimation(blink)
            loadAndShowTips(weatherKey)
        }

        // 🔊 Speak tips button
        binding.speakTipButton.setOnClickListener {
            if (!isTtsInitialized) {
                Toast.makeText(this, "TTS not ready yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val speechText = buildString {
                append("Here are your smart plant tips. ")

                append("General Tips: ")
                append(currentTipsMap["general1"]).append(". ")
                append(currentTipsMap["general2"]).append(". ")
                append(currentTipsMap["general3"]).append(". ")

                append("Watering Tips: ")
                append(currentTipsMap["watering1"]).append(". ")
                append(currentTipsMap["watering2"]).append(". ")
                append(currentTipsMap["watering3"]).append(". ")

                append("Sunlight Tips: ")
                append(currentTipsMap["sunlight1"]).append(". ")
                append(currentTipsMap["sunlight2"]).append(". ")

                append("Soil Tips: ")
                append(currentTipsMap["soil1"]).append(". ")
                append(currentTipsMap["soil2"]).append(". ")

                append("Temperature Tips: ")
                append(currentTipsMap["temp1"]).append(". ")
                append(currentTipsMap["temp2"]).append(". ")

                append("Fertilizer Tips: ")
                append(currentTipsMap["fertilizer1"]).append(". ")
                append(currentTipsMap["fertilizer2"]).append(". ")

                append("Disease Care: ")
                append(currentTipsMap["disease1"]).append(". ")
                append(currentTipsMap["disease2"]).append(". ")
                append(currentTipsMap["disease3"]).append(". ")

                append("Signs to Watch: ")
                append(currentTipsMap["signs1"]).append(". ")
                append(currentTipsMap["signs2"]).append(". ")

                append("Pruning Tips: ")
                append(currentTipsMap["prune1"]).append(". ")
                append(currentTipsMap["prune2"]).append(". ")

                append("Plant Categories: ")
                append(currentTipsMap["category1"]).append(". ")
                append(currentTipsMap["category2"]).append(". ")
                append(currentTipsMap["category3"]).append(". ")

                append("Seasonal Tips: ")
                append(currentTipsMap["season1"]).append(". ")
                append(currentTipsMap["season2"]).append(". ")
                append(currentTipsMap["season3"]).append(". ")

                append("Dos and Don'ts: ")
                append(currentTipsMap["dos1"]).append(". ")
                append(currentTipsMap["donts1"]).append(". ")
            }
                val cleanedText = stripSymbols(speechText)
                tts.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, null)

        }
    }

    private fun extractCity(text: String): String {
        return text.split("•").firstOrNull()?.trim()?.let { "📍 $it" } ?: "📍 Unknown"
    }

    private fun getTipsForWeather(weather: String): Map<String, String> {
        return when (weather) {
            "clear" -> mapOf(
                "general1" to "✅ Provide shade for delicate plants.",
                "general2" to "✅ Water early morning to avoid evaporation.",
                "general3" to "✅ Remove dry weeds regularly.",
                "watering1" to "✅ Increase watering frequency.",
                "watering2" to "✅ Mulch to retain moisture.",
                "watering3" to "❌ Don’t water under direct midday sun.",
                "sunlight1" to "✅ Protect from scorching sun.",
                "sunlight2" to "✅ Rotate pots for even exposure.",
                "soil1" to "✅ Keep soil cool with mulch.",
                "soil2" to "✅ Use moisture-retaining compost.",
                "temp1" to "✅ Avoid pot exposure to hot surfaces.",
                "temp2" to "✅ Use reflective covers if needed.",
                "fertilizer1" to "✅ Fertilize in early mornings.",
                "fertilizer2" to "✅ Use liquid fertilizer to avoid burn.",
                "disease1" to "✅ Watch for sunburn spots.",
                "disease2" to "✅ Use seaweed spray as a preventive.",
                "disease3" to "✅ Remove scorched leaves.",
                "signs1" to "✅ Wilting, dry edges.",
                "signs2" to "✅ Yellowing tips.",
                "prune1" to "✅ Remove sun-damaged leaves.",
                "prune2" to "✅ Thin canopy to reduce heat trap.",
                "category1" to "✅ Veggies need partial shade.",
                "category2" to "✅ Fruits require direct light.",
                "category3" to "✅ Ornamentals prefer filtered light.",
                "season1" to "✅ Summer: Water deeply every 2 days.",
                "season2" to "✅ Use shade nets outdoors.",
                "season3" to "✅ Mist leaves for humidity.",
                "dos1" to "✅ Do: Cover soil, hydrate regularly.",
                "donts1" to "❌ Don’t leave pots on concrete in sun."
            )

            "rain" -> mapOf(
                "general1" to "✅️ Ensure pots have drainage holes.",
                "general2" to "✅️ Clear stagnant water nearby.",
                "general3" to "✅️ Wash off leaves after storm.",
                "watering1" to "✅️ Skip watering if it rained.",
                "watering2" to "✅️ Only water if soil is dry.",
                "watering3" to "❌ Don’t overwater wet roots.",
                "sunlight1" to "✅️ Move indoor plants to balconies.",
                "sunlight2" to "✅️ Clean windows for better light.",
                "soil1" to "✅️ Raise pots to avoid soggy soil.",
                "soil2" to "✅️ Add perlite for drainage.",
                "temp1" to "✅️ Watch for sudden temperature drops.",
                "temp2" to "✅️ Cover sensitive plants.",
                "fertilizer1" to "✅️ Avoid fertilizing soggy soil.",
                "fertilizer2" to "✅️ Use foliar spray instead.",
                "disease1" to "✅️ Fungal spots are common.",
                "disease2" to "✅️ Use neem oil spray.",
                "disease3" to "✅ Isolate infected ones.",
                "signs1" to "✅ Black spots, mildew.",
                "signs2" to "✅ Mushy stems.",
                "prune1" to "✅ Prune affected leaves.",
                "prune2" to "'✅️ Trim crowded areas.",
                "category1" to "✅ Fruits may split if overwatered.",
                "category2" to "✅ Veggies prone to rot.",
                "category3" to "✅ Ornamentals may suffer mold.",
                "season1" to "✅ Monsoon: Add gravel for drainage.",
                "season2" to "✅ Watch root rot signs.",
                "season3" to "✅ Use raised garden beds.",
                "dos1" to "✅ Do: Improve airflow.",
                "donts1" to "❌ Don’t leave soil soggy."
            )

            "cloud" -> mapOf(
                "general1" to "✅ Keep plants in bright shade.",
                "general2" to "✅ Clean leaves for max absorption.",
                "general3" to "✅ Avoid overwatering due to less evaporation.",
                "watering1" to "✅ Check soil manually before watering.",
                "watering2" to "✅ Water less than usual.",
                "watering3" to "❌ Don’t assume plants are thirsty.",
                "sunlight1" to "✅ Maximize exposure to any light.",
                "sunlight2" to "✅ Supplement with grow lights if needed.",
                "soil1" to "✅ Loosen topsoil to breathe.",
                "soil2" to "✅ Avoid soil compaction.",
                "temp1" to "✅ Monitor cold spells.",
                "temp2" to "✅ Protect tropical plants.",
                "fertilizer1" to "✅ Reduce fertilizing frequency.",
                "fertilizer2" to "✅ Apply only when sun peeks out.",
                "disease1" to "✅ Prevent mildew.",
                "disease2" to "✅ Avoid wetting leaves.",
                "disease3" to "✅ Increase airflow.",
                "signs1" to "✅ Yellow leaves.",
                "signs2" to "✅ Droopy posture.",
                "prune1" to "✅ Prune to open up for light.",
                "prune2" to "✅ Remove lichen/fungus growth.",
                "category1" to "✅ Fruits need sunny days post-cloud.",
                "category2" to "✅ Veggies grow slower.",
                "category3" to "✅ Ornamentals may lose color.",
                "season1" to "✅ Early Winter: Add warmth via mulch.",
                "season2" to "✅ Use compost tea for immunity.",
                "season3" to "✅ Water only dry soil.",
                "dos1" to "✅ Do: Clean leaves regularly.",
                "donts1" to "❌ Don’t keep under dense shade."
            )

            "thunder" -> mapOf(
                "general1" to "✅ Move pots to sheltered areas.",
                "general2" to "✅ Tie tall plants securely.",
                "general3" to "✅ Avoid pruning before storm.",
                "watering1" to "✅ Avoid watering during storm.",
                "watering2" to "✅ Resume once weather stabilizes.",
                "watering3" to "❌ Don’t leave water trays full.",
                "sunlight1" to "✅ Wait for storm to clear.",
                "sunlight2" to "✅ Clean dust after rain.",
                "soil1" to "✅ Soil may compact — loosen gently.",
                "soil2" to "✅ Check drainage again.",
                "temp1" to "✅ Sudden chill? Cover plants.",
                "temp2" to "✅ Use fleece on cold nights.",
                "fertilizer1" to "✅ Pause feeding until safe.",
                "fertilizer2" to "✅ Boost after storm ends.",
                "disease1" to "✅ Bacteria spreads fast — isolate.",
                "disease2" to "✅ Spray copper fungicide.",
                "disease3" to "✅ Trim infected parts.",
                "signs1" to "✅ Leaf curl, wet patches.",
                "signs2" to "✅ Bad smell from roots.",
                "prune1" to "✅ Post-storm remove damage.",
                "prune2" to "✅ Don’t cut fresh wounds.",
                "category1" to "✅ Fruit trees need staking.",
                "category2" to "✅ Veggies in polyhouse preferred.",
                "category3" to "✅ Ornamental pots should be indoors.",
                "season1" to "✅ Storm Season: Avoid planting new.",
                "season2" to "✅ Use mulch post-rain.",
                "season3" to "✅ Shelter is priority.",
                "dos1" to "✅ Do: Dry pots quickly after rain.",
                "donts1" to "❌ Don’t prune before storm."
            )
            else -> getTipsForWeather("clear")
        }
    }

    private fun loadAndShowTips(weatherKey: String) {
        val tipsMap = getTipsForWeather(weatherKey)
        currentTipsMap = tipsMap // 🔁 Cache tips for TTS use
        binding.tipsContainer.removeAllViews()

        val tipSectionView = layoutInflater.inflate(R.layout.tip_sections, binding.tipsContainer, false)
        binding.tipsContainer.addView(tipSectionView)

        with(tipSectionView) {
            findViewById<TextView>(R.id.general1).text = tipsMap["general1"]
            findViewById<TextView>(R.id.general2).text = tipsMap["general2"]
            findViewById<TextView>(R.id.general3).text = tipsMap["general3"]
            findViewById<TextView>(R.id.watering1).text = tipsMap["watering1"]
            findViewById<TextView>(R.id.watering2).text = tipsMap["watering2"]
            findViewById<TextView>(R.id.watering3).text = tipsMap["watering3"]
            findViewById<TextView>(R.id.sunlight1).text = tipsMap["sunlight1"]
            findViewById<TextView>(R.id.sunlight2).text = tipsMap["sunlight2"]
            findViewById<TextView>(R.id.soil1).text = tipsMap["soil1"]
            findViewById<TextView>(R.id.soil2).text = tipsMap["soil2"]
            findViewById<TextView>(R.id.temp1).text = tipsMap["temp1"]
            findViewById<TextView>(R.id.temp2).text = tipsMap["temp2"]
            findViewById<TextView>(R.id.fertilizer1).text = tipsMap["fertilizer1"]
            findViewById<TextView>(R.id.fertilizer2).text = tipsMap["fertilizer2"]
            findViewById<TextView>(R.id.disease1).text = tipsMap["disease1"]
            findViewById<TextView>(R.id.disease2).text = tipsMap["disease2"]
            findViewById<TextView>(R.id.disease3).text = tipsMap["disease3"]
            findViewById<TextView>(R.id.signs1).text = tipsMap["signs1"]
            findViewById<TextView>(R.id.signs2).text = tipsMap["signs2"]
            findViewById<TextView>(R.id.prune1).text = tipsMap["prune1"]
            findViewById<TextView>(R.id.prune2).text = tipsMap["prune2"]
            findViewById<TextView>(R.id.category1).text = tipsMap["category1"]
            findViewById<TextView>(R.id.category2).text = tipsMap["category2"]
            findViewById<TextView>(R.id.category3).text = tipsMap["category3"]
            findViewById<TextView>(R.id.season1).text = tipsMap["season1"]
            findViewById<TextView>(R.id.season2).text = tipsMap["season2"]
            findViewById<TextView>(R.id.season3).text = tipsMap["season3"]
            findViewById<TextView>(R.id.dos1).text = tipsMap["dos1"]
            findViewById<TextView>(R.id.donts1).text = tipsMap["donts1"]
        }
    }

    override fun onPause() {
        super.onPause()
        if (::tts.isInitialized && tts.isSpeaking) {
            tts.stop() // Stop ongoing speech
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown() // Free resources
        }
    }
    private fun stripSymbols(input: String): String {
        return input.replace("[✅❌🌿💧☀️🌱🌡️🦠🔍✂️🌼📆]".toRegex(), "")
    }

}
