import com.android.build.api.variant.BuildConfigField

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.roomexample"
    compileSdk = 36

    // Load secrets from local.properties
    val localProperties = org.jetbrains.kotlin.konan.properties.Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }

    defaultConfig {
        applicationId = "com.example.mangiaebasta"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add BuildConfig fields
        buildConfigField("String", "API_BASE_URL", "\"${localProperties.getProperty("API_BASE_URL", "")}\"")
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${localProperties.getProperty("MAPBOX_ACCESS_TOKEN", "")}\"")
        buildConfigField("String", "MAPBOX_STYLE_URL", "\"${localProperties.getProperty("MAPBOX_STYLE_URL", "")}\"")
    }

    buildTypes {
        debug {
            // Add Mapbox token as a resource value for debug builds
            resValue("string", "mapbox_access_token", localProperties.getProperty("MAPBOX_ACCESS_TOKEN", ""))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            // Add Mapbox token as a resource value for release builds
            resValue("string", "mapbox_access_token", localProperties.getProperty("MAPBOX_ACCESS_TOKEN", ""))
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    androidResources {
        generateLocaleConfig = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    sourceSets {
        getByName("debug") {
            java.srcDirs("build/generated/ksp/debug/java", "build/generated/ksp/debug/kotlin")
        }
        getByName("release") {
            java.srcDirs("build/generated/ksp/release/java", "build/generated/ksp/release/kotlin")
        }
    }
}

androidComponents {
    onVariants { variant ->
        variant.buildConfigFields?.put(
            "BUILD_TIME", BuildConfigField(
                "String", "\"${System.currentTimeMillis()}\"", "build timestamp"
            )
        )
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
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.rxjava2)
    implementation(libs.androidx.room.rxjava3)
    implementation(libs.androidx.room.guava)
    implementation(libs.androidx.room.paging)
    implementation(libs.ktor.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.io.ktor.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json.v210)
    implementation(libs.android)
    implementation(libs.maps.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidxComposeMaterial3)
    implementation(libs.androidxComposeMaterialIconsExtended)
    implementation(libs.kotlinxCoroutinesPlayServices)
    implementation(libs.play.services.location)
    implementation(libs.datastore.preferences)
    implementation(libs.datastore)
    implementation(libs.androidx.datastore.preferences.rxjava2)
    implementation(libs.androidx.datastore.preferences.rxjava3)
    implementation(libs.gson)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

