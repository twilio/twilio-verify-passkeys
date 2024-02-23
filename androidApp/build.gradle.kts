plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.hilt)
  alias(libs.plugins.kotlinSerialization)
  kotlin("kapt")
}

repositories {
  mavenLocal()
}

android {
  namespace = "com.twilio.passkeys.android"
  compileSdk = 34
  defaultConfig {
    applicationId = "com.twilio.passkeys.android"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  signingConfigs {
    create("release") {
      storeFile = file("passkeys-release-key.keystore")
      storePassword = "twilio-verify-passkeys"
      keyAlias = "twilio-verify-passkeys"
      keyPassword = "twilio-verify-passkeys"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("release")
    }
    getByName("debug") {
      signingConfig = signingConfigs.getByName("release")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

val versionCode: String by extra
dependencies {
  debugImplementation(projects.shared)
  releaseImplementation("com.twilio:twilio-verify-passkeys-android:$versionCode")
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.compose.material3)
  implementation(libs.androidx.activity.compose)
  implementation(libs.hilt.android)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.constraintlayout.compose)
  implementation(libs.androidx.constraintlayout)
  kapt(libs.hilt.android.compiler)
  // Retrofit
  implementation(libs.retrofit)
  implementation(libs.retrofit2.kotlinx.serialization.converter)
  implementation(libs.logging.interceptor)
  implementation(libs.kotlinx.serialization.json)

  debugImplementation(libs.compose.ui.tooling)
}
