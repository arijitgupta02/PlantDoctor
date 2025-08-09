plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.plantdoctor" // Change if your package is different
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.plantdoctor"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val apiKey = project.findProperty("WEATHER_API_KEY") as? String ?: ""
        buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")



    // AndroidX Core UI support
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // PyTorch
    implementation("org.pytorch:pytorch_android:1.13.1")
    implementation("org.pytorch:pytorch_android_torchvision:1.13.1")

    // Testing (optional)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Material Design
    implementation("com.google.android.material:material:1.9.0")

    //airbnb
    implementation("com.airbnb.android:lottie:6.3.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Coroutines (if using suspend functions)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // TensorFlow Lite core
    implementation("org.tensorflow:tensorflow-lite:2.14.0")

    // Optional: TensorFlow Lite Support Library (for easier tensor handling)
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // Optional: TensorFlow Lite GPU Delegate (if using GPU)
    implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0")





}
