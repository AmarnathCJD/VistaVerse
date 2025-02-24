plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.amarnath.helth"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.amarnath.helth"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(libs.androidx.espresso.core)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.compose.material3:material3:1.1.1") // or latest version
    implementation("androidx.compose.material:material-icons-extended:1.5.4") // For Icons.Filled
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.6.0") // Or your compose ui tooling version
    implementation("androidx.compose.ui:ui-tooling:1.6.0") // Or your compose ui tooling version
    implementation("androidx.compose.ui:ui:1.6.0") // Or your compose ui version
    implementation("androidx.compose.runtime:runtime:1.6.0") // Or your compose runtime version
    implementation("androidx.compose.foundation:foundation-layout:1.6.0") // Or your compose foundation layout version
    implementation("androidx.compose.material3:material3-android:1.1.1") // Or latest version

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)

    implementation(libs.okhttp)
    implementation(libs.generativeai)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)
}