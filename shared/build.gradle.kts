/*
 * Copyright © 2024 Twilio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.kover)
  alias(libs.plugins.dokka)
  signing
  `maven-publish`
  id("com.twilio.apkscale")
  id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
  id("co.touchlab.skie") version "0.6.1"
}

val dokkaOutputDir = "$buildDir/dokka"

tasks.dokkaHtml {
  outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
  delete(dokkaOutputDir)
}
val javadocJar =
  tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javaDoc")
    from(dokkaOutputDir)
  }

val versionCode: String by extra
version = versionCode
val libId = "twilio-verify-passkeys"

afterEvaluate {
  publishing {
    publications {
      withType<MavenPublication> {
        when (name) {
          "kotlinMultiplatform" -> {
            this.artifactId = "$libId-common"
          }

          else -> {
            this.artifactId = "$libId-${name.lowercase()}"
          }
        }
        groupId = "com.twilio"
        artifact(javadocJar)

        pom {
          name.set("Twilio Verify Passkeys Android")
          description.set(
            """
            Twilio Passkeys SDK enables developers to easily add Passkeys into their existing authentication flows 
            within their own mobile applications. The Verify Passkeys SDK supports passkeys creation and authentication 
            using the FIDO/WebAuthn industry standard.
            """.trimIndent(),
          )
          url.set("https://github.com/twilio/twilio-verify-passkeys")
          licenses {
            license {
              name.set("Apache License, Version 2.0")
              url.set("https://github.com/twilio/twilio-verify-passkeys/blob/main/LICENSE")
            }
          }
          developers {
            developer {
              id.set("Twilio")
              name.set("Twilio")
            }
          }
          scm {
            connection.set("scm:git:github.com/twilio/twilio-verify-passkeys.git")
            developerConnection.set("scm:git:github.com/twilio/twilio-verify-passkeys.git")
            url.set("https://github.com/twilio/twilio-verify-passkeys/tree/main")
          }
        }
      }
    }
  }
}

// TODO: remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
  dependsOn(project.tasks.withType(Sign::class.java))
}

signing {
  sign(publishing.publications)
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget {
    mavenPublication {
      artifactId = "$libId-android"
    }
    publishLibraryVariants("release")

    compilations.all {
      kotlinOptions {
        jvmTarget = "17"
      }
    }
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach {
    it.binaries.framework {
      baseName = "TwilioPasskeys"
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        // put your multiplatform dependencies here
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlin.coroutines.core)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlin.coroutines.test)
      }
    }
  }

  multiplatformSwiftPackage {
    packageName("PasskeyTestIOS")
    swiftToolsVersion("5.3")
    targetPlatforms {
      iOS { v("13") }
    }
    outputDirectory(File(rootDir, "/"))
  }
}

koverReport {
  filters {
    excludes {
      classes("*TestActivity")
    }
  }
  defaults {
    mergeWith("debug")
  }
  verify {
    rule {
      entity = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION
      bound {
        // lower bound
        minValue = 85

        // upper bound
        maxValue = 100

        // specify which units to measure coverage for
        metric = kotlinx.kover.gradle.plugin.dsl.MetricType.INSTRUCTION

        // specify an aggregating function to obtain a single value that will be checked against the lower and upper boundaries
        aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
      }
    }
  }
}

android {
  namespace = "com.twilio.passkeys"
  compileSdk = 34
  defaultConfig {
    minSdk = 24
    targetSdkVersion(34) // Don't remove this, apkscale plugin needs it
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      excludes += "/META-INF/LICENSE.md"
      excludes += "/META-INF/LICENSE-notice.md"
    }
  }

  apkscale {
    abis = setOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
  }

  task("generateSizeReport") {
    dependsOn("assembleRelease", "measureSize")
    description = "Calculate Passkeys Android SDK Size Impact"
    group = "Reporting"

    doLast {
      var sizeReport =
        "### Size impact\n" +
          "\n" +
          "| ABI             | APK Size Impact |\n" +
          "| --------------- | --------------- |\n"
      val apkscaleOutputFile = file("$buildDir/apkscale/build/outputs/reports/apkscale.json")
      val jsonSlurper = groovy.json.JsonSlurper()
      val apkscaleOutput = jsonSlurper.parseText(apkscaleOutputFile.readText()) as List<*>
      val releaseOutput = apkscaleOutput[0] as Map<*, *>
      val sizes = releaseOutput["size"] as Map<String, String>
      sizes.forEach { (arch, sizeImpact) ->
        sizeReport += "| ${arch.padEnd(16)}| ${sizeImpact.padEnd(16)}|\n"
      }
      val sizeReportDir = "$buildDir/outputs/sizeReport"
      mkdir(sizeReportDir)
      val targetFile = file("$sizeReportDir/AndroidSDKSizeReport.txt")
      targetFile.createNewFile()
      targetFile.writeText(sizeReport)
    }
  }

  dependencies {
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.auth)
    testImplementation(libs.mockk.android)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.activity.ktx)
  }
}
