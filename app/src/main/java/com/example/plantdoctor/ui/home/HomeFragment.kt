package com.example.plantdoctor.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.plantdoctor.MainActivity
import com.example.plantdoctor.TipsActivity
import com.example.plantdoctor.AboutActivity
import com.example.plantdoctor.HistoryActivity
import com.example.plantdoctor.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt
import com.example.plantdoctor.BuildConfig
import android.util.Log
import com.example.plantdoctor.R
import com.example.plantdoctor.SeasonUtils


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val CAMERA_PERMISSION_CODE = 101
    private val LOCATION_PERMISSION_CODE = 102
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var imageFile: File? = null

    private val cameraLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.TakePicture()) { success ->
            if (success && imageFile != null) {
                val imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    imageFile!!
                )
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Image capture failed", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.greetingText.text = "👋 ${getGreetingMessage()}, Arijit!"
        binding.tvSeasonalTip.text = SeasonUtils.getSeasonalTip()
        val season = SeasonUtils.getCurrentSeason()
        val seasonImage = when (season) {
            "Monsoon" -> R.drawable.bg_monsoon
            "Summer" -> R.drawable.bg_summer
            "Winter" -> R.drawable.bg_winter
            "Autumn" -> R.drawable.bg_autumn
            else -> null
        }

        seasonImage?.let {
            binding.seasonCardBackground.setImageResource(it)
            binding.seasonCardBackground.visibility = View.VISIBLE
        } ?: run {
            binding.seasonCardBackground.visibility = View.GONE
        }




        // Camera button
        binding.captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                launchCamera()
            }
        }

        binding.tipsCard.setOnClickListener {
            val weatherText = binding.weatherInfo.text.toString()
            val suggestionText = binding.suggestionText.text.toString()

            if (weatherText.contains("•") && !suggestionText.contains("Loading", true)) {
                val tipsIntent = Intent(requireContext(), TipsActivity::class.java).apply {
                    putExtra("weather", weatherText)
                    putExtra("suggestion", suggestionText)
                }
                startActivity(tipsIntent)
            } else {
                Toast.makeText(requireContext(), "Please wait for weather to load...", Toast.LENGTH_SHORT).show()
            }
        }
        binding.aboutCard.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intent)
        }

        binding.historyCard.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }


        // Request Location Permission (Updated ✅)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            fetchLocationAndWeather() // ✅ Only call if permission already granted
        }
    }


    private fun getGreetingMessage(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning 🌞"
            in 12..16 -> "Good Afternoon ☀️"
            in 17..20 -> "Good Evening 🌇"
            else -> "Good Night 🌙"
        }
    }

    private fun launchCamera() {
        imageFile = File(requireContext().cacheDir, "captured_image.jpg")
        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            imageFile!!
        )
        cameraLauncher.launch(imageUri)
    }

    private fun fetchLocationAndWeather() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val cancellationTokenSource = com.google.android.gms.tasks.CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                fetchWeatherData(location)
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to fetch location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeatherData(location: Location) {
        val apiKey = BuildConfig.WEATHER_API_KEY
        Log.d("HomeFragment", "Using API Key: $apiKey")

        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=${location.latitude}&lon=${location.longitude}&appid=$apiKey"

        Log.d("HomeFragment", "Fetching weather from: $url")

        Thread {
            try {
                val request = Request.Builder().url(url).build()
                val response = OkHttpClient().newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.e("HomeFragment", "Weather API request failed: ${response.code}")
                    return@Thread
                }

                val json = response.body?.string()
                Log.d("HomeFragment", "API Response: $json")

                if (json != null) {
                    val obj = JSONObject(json)
                    val city = obj.getString("name")
                    val weather = obj.getJSONArray("weather").getJSONObject(0).getString("main")
                    val kelvin = obj.getJSONObject("main").getDouble("temp")
                    val celsius = (kelvin - 273.15).roundToInt()

                    activity?.runOnUiThread {
                        binding.weatherInfo.text = "📍 $city • $weather • $celsius°C"
                        binding.suggestionText.text = getSuggestionFor(weather)

                        val animationRes = when (weather.lowercase()) {
                            "rain", "drizzle" -> R.raw.rain
                            "clear" -> R.raw.sunny
                            "clouds" -> R.raw.cloud
                            "thunderstorm" -> R.raw.storm
                            else -> R.raw.cloud
                        }
                        binding.weatherAnimation.setAnimation(animationRes)
                        binding.weatherAnimation.playAnimation()

                    }
                } else {
                    Log.e("HomeFragment", "Empty JSON response from weather API")
                }
            } catch (e: IOException) {
                Log.e("HomeFragment", "Exception during weather fetch: ${e.message}")
                e.printStackTrace()
            }
        }.start()
    }


    private fun showError(message: String) {
        activity?.runOnUiThread {
            binding.weatherInfo.text = "Failed to load weather"
            binding.suggestionText.text = "Try again later"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun getSuggestionFor(condition: String): String {
        return when (condition.lowercase()) {
            "rain", "drizzle" -> "Avoid overwatering 🌧️"
            "clear" -> "Perfect day for sunlight ☀️"
            "clouds" -> "Low sunlight, monitor growth ☁️"
            "thunderstorm" -> "Keep plants sheltered ⛈️"
            else -> "Keep observing your plant's needs 🍀"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndWeather()
            } else {
                Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 👉 Launch camera immediately after permission granted
                launchCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
