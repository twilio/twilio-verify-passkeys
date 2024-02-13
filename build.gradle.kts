import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

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
