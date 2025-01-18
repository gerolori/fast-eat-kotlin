plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Room setup
    id("com.google.devtools.ksp")
    // Ktor setup
    id("kotlinx-serialization")
}

android {
    namespace = "com.example.roomexample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mangiaebasta"
        minSdk = 34
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Room setup
    implementation(libs.androidx.room.runtime)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    // optional - RxJava2 support for Room
    implementation(libs.androidx.room.rxjava2)
    // optional - RxJava3 support for Room
    implementation(libs.androidx.room.rxjava3)
    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation(libs.androidx.room.guava)
    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)
    // Ktor setup
    // Core
    implementation(libs.ktor.ktor.client.core)
    implementation(libs.ktor.client.android)
    // Logging
    implementation(libs.ktor.client.logging)
    // JSON Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.ktor.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json.v210)
    implementation(libs.android)
    implementation(libs.maps.compose)
    // Navbar setup
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp(libs.androidx.room.compiler)
    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor(libs.androidx.room.compiler)
}
