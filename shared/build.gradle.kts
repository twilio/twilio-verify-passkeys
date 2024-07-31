/*
 * Copyright © 2024 Twilio Inc.
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
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.kover)
  alias(libs.plugins.dokka)
  signing
  `maven-publish`
  id("com.twilio.apkscale")
  id("co.touchlab.skie") version "0.6.1"
}
buildscript {
  dependencies {
    classpath(libs.dokka.versioning.plugin)
  }
}
dependencies {
  dokkaPlugin(libs.dokka.versioning.plugin)
}

val libId = "twilio-verify-passkeys"
val sdkVersionName: String by extra
version = sdkVersionName

val dokkaOutputDir = "$buildDir/dokka"
val dokkaOutputVersionDir = "$dokkaOutputDir/$version"
tasks.dokkaHtml {
  moduleName.set(libId)
  outputDirectory.set(file(dokkaOutputVersionDir))

  val currentVersion = version.toString()
  pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
    version = currentVersion
    olderVersionsDir = file(dokkaOutputDir)
    renderVersionsNavigationOnAllPages = true
  }

  doLast {
    // This folder contains the latest documentation with all
    // previous versions included, so it's ready to be published.
    file(dokkaOutputVersionDir).copyRecursively(file("../docs"), overwrite = true)

    // Only once current documentation has been safely moved,
    // remove previous versions bundled in it. They will not
    // be needed in future builds, it's just overhead.
    file(dokkaOutputVersionDir).resolve("older").deleteRecursively()
  }
}

val javadocJar =
  tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javaDoc")
    from(dokkaOutputVersionDir)
  }

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

  val xcFrameworkName = "TwilioPasskeysAuthentication"
  val xcf = XCFramework(xcFrameworkName)
  val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())

  iosTargets.forEach {
    it.binaries.framework {
      baseName = xcFrameworkName
      xcf.add(this)
    }
  }

  // https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
  targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
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
