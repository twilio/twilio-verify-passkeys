#!/usr/bin/env kotlin
import kotlin.system.exitProcess

if (args.size != 2) {
  println("Usage ./requires_release.kts {{from_tag_string}} {{platform}}")
  exitProcess(400)
}

val gitTag = args[0]
val platform = if (args[1].lowercase() == Platform.KMP.name.lowercase()) {
  Platform.KMP
} else if (args[1].lowercase() == Platform.IOS.name.lowercase()) {
  Platform.IOS
} else {
  println("Bad usage: {{changelog_for}} parameter accepts KMP or iOS")
  exitProcess(400)
}
println(shouldRelease(gitTag, platform))

fun shouldRelease(fromTag: String, platform: Platform): VersionBumpType {
  val commits = getCommitHistory(fromTag)
  val tmpCommits = commits.filter { !it.contains("!") }.filter { !it.contains("feat") }.filter { !it.contains("fix") }
  return determineReleaseType(tmpCommits, platform)
}

fun determineReleaseType(commits: List<String>, platform: Platform): VersionBumpType {
  val isBreakingChange = isBreakingChange(commits, platform)
  val isFeature = isFeature(commits, platform)
  val isFix = isFix(commits, platform)

  val versionBumpType = when {
    isBreakingChange -> VersionBumpType.MAJOR
    isFeature -> VersionBumpType.MINOR
    isFix -> VersionBumpType.PATCH
    else -> VersionBumpType.NONE
  }

  return versionBumpType
}

fun isBreakingChange(commits: List<String>, platform: Platform): Boolean {
  val isBreakingChange = if (platform == Platform.IOS) {
    commits.any { it.matches("^.*(\\[(?i)ios\\])?(?!\\[(?i)android\\])(\\(.+\\))?!:.+".toRegex()) }
  } else {
    commits.any { it.matches("^.*!.*:.*\$".toRegex()) }
  }
  return isBreakingChange
}

fun isFeature(commits: List<String>, platform: Platform): Boolean {
  val isBreakingChange = if (platform == Platform.IOS) {
    commits.any { it.matches("^feat(\\[(?i)ios\\])?(?!\\[(?i)android\\])(\\(.+\\))?:.+".toRegex()) }
  } else {
    commits.any { it.matches("^feat.*:.*\$".toRegex()) }
  }
  return isBreakingChange
}

fun isFix(commits: List<String>, platform: Platform): Boolean {
  val isBreakingChange = if (platform == Platform.IOS) {
    commits.any { it.matches("^fix(\\[(?i)ios\\])?(?!\\[(?i)android\\])(\\(.+\\))?:.+".toRegex()) }
  } else {
    commits.any { it.matches("^fix.*:.*\$".toRegex()) }
  }
  return isBreakingChange
}

fun getCommitHistory(fromTag: String): List<String> {
  val process: Process = ProcessBuilder("git", "log", "--pretty=format:%s", "$fromTag..HEAD").start()
  val output = process.inputStream.bufferedReader().readLines()
  process.waitFor()

  return output
}

enum class VersionBumpType {
  MAJOR, MINOR, PATCH, NONE
}

enum class Platform {
  KMP,
  IOS
}

