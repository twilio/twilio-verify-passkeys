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

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

buildscript {
  dependencies {
    classpath(libs.apkscale)
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }
}

plugins {
  // trick: for the same plugin versions in all sub-modules
  alias(libs.plugins.androidApplication).apply(false)
  alias(libs.plugins.androidLibrary).apply(false)
  alias(libs.plugins.kotlinAndroid).apply(false)
  alias(libs.plugins.kotlinMultiplatform).apply(false)
  alias(libs.plugins.hilt).apply(false)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.jvm)
  alias(libs.plugins.kover).apply(false)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
  alias(libs.plugins.nexus)
}

allprojects {
  apply<KtlintPlugin>()
  ktlint {
    version = "1.1.0"
    verbose.set(true)
    debug.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(true)
    reporters {
      reporter(ReporterType.JSON)
    }
  }

  tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask> {
    reportsOutputDirectory.set(
      project.layout.buildDirectory.dir("$buildDir/reports/ktlint"),
    )
  }

  apply<DetektPlugin>()
  detekt {
    ignoreFailures = true
    parallel = true // can lead to speedups in larger projects
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config.setFrom("$rootDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    source.setFrom(
      "src/main/java",
      "src/main/kotlin",
      "src/androidMain/kotlin",
      "src/androidUnitTest/kotlin",
      "src/androidInstrumentedTest/kotlin",
      "src/commonTest/kotlin",
      "src/commonMain/kotlin",
      "src/iosMain/kotlin",
      "src/iosTest/kotlin",
      "src/jsMain/kotlin"
    )
  }

  val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/detekt.xml"))
  }
  subprojects {
    tasks.withType<Detekt>().configureEach {
      finalizedBy(reportMerge)
    }

    reportMerge {
      input.from(tasks.withType<Detekt>().map { it.xmlReportFile }) // or .sarifReportFile
    }
  }
}

nexusPublishing {
  repositories {
    sonatype {
      username = getPropertyValue("OSSRH_USERNAME")
      password = getPropertyValue("OSSRH_PASSWORD")
      stagingProfileId = getPropertyValue("SONATYPE_STAGING_PROFILE_ID")
    }
  }
}

task("sonatypeTwilioPasskeysStagingRepositoryUpload", GradleBuild::class) {
  description = "Publish Twilio Passkeys to nexus staging repository"
  group = "Publishing"
  buildName = "TwilioPasskeys"
  tasks =
    listOf(
      ":shared:publishToSonatype",
      "closeSonatypeStagingRepository",
    )
  startParameter.projectProperties.plusAssign(
    gradle.startParameter.projectProperties + mavenPublishCredentials(),
  )
}

task("mavenLocalTwilioPasskeysReleaseUpload", GradleBuild::class) {
  description = "Publish Twilio Passkeys to maven local"
  group = "Publishing"
  buildName = "TwilioPasskeys"
  tasks =
    listOf(
      ":shared:publishToMavenLocal",
    )
  startParameter.projectProperties.plusAssign(
    gradle.startParameter.projectProperties + mavenPublishCredentials(),
  )
}

fun mavenPublishCredentials(): Map<String, String> {
  return mapOf(
    "signing.keyId" to getPropertyValue("SIGNING_KEY_ID"),
    "signing.password" to getPropertyValue("SIGNING_PASSWORD"),
    "signing.secretKeyRingFile" to getPropertyValue("SIGNING_SECRET_KEY_RING_FILE"),
    "ossrhUsername" to getPropertyValue("OSSRH_USERNAME"),
    "ossrhPassword" to getPropertyValue("OSSRH_PASSWORD"),
    "sonatypeStagingProfileId" to getPropertyValue("SONATYPE_STAGING_PROFILE_ID"),
  )
}

fun getPropertyValue(key: String): String {
  val property =
    if (project.hasProperty(key)) {
      project.property(key) as String
    } else {
      System.getenv(key)
    }
  if (property == null) {
    println("Warning: Property or environment variable '$key' is not defined.")
    return ""
  }
  return property
}
