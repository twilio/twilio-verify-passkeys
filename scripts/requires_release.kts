#!/usr/bin/env kotlin
import kotlin.system.exitProcess

if (args.size != 2) {
  println("Usage ./requires_release.kts {{from_tag_string}} {{check_only_ios_boolean}}")
  exitProcess(400)
}

val gitTag = args[0]
val checkOnlyIOS = args[1].toBooleanStrict()

println(shouldRelease(gitTag, checkOnlyIOS))

fun shouldRelease(fromTag: String, checkOnlyIOS: Boolean): Boolean {
  val commits = getCommitHistory(fromTag)

  return determineReleaseType(commits, checkOnlyIOS) != ReleaseType.NONE
}

fun getCommitHistory(fromTag: String): List<String> {
  val process: Process = ProcessBuilder("git", "log", "--pretty=format:%s", "$fromTag..HEAD").start()
  val output = process.inputStream.bufferedReader().readLines()
  process.waitFor()

  return output
}

fun determineReleaseType(commits: List<String>, checkOnlyIOS: Boolean): ReleaseType {
  val releaseType: ReleaseType = if (checkOnlyIOS) {
    when {
      commits.any { it.matches("^breaking(\\[(?i)ios\\])?(?!\\[(?i)android\\]).*\$".toRegex()) } -> ReleaseType.MAJOR
      commits.any { it.matches("^feat(\\[(?i)ios\\])?(?!\\[(?i)android\\]).*\$".toRegex()) } -> ReleaseType.MINOR
      commits.any { it.matches("^fix(\\[(?i)ios\\])?(?!\\[(?i)android\\]).*\$".toRegex()) } -> ReleaseType.PATCH
      else -> ReleaseType.NONE
    }
  } else {
    when {
      commits.any { it.matches("^breaking.*:.*\$".toRegex()) } -> ReleaseType.MAJOR
      commits.any { it.matches("^feat.*:.*\$".toRegex()) } -> ReleaseType.MINOR
      commits.any { it.matches("^fix.*:.*\$".toRegex()) } -> ReleaseType.PATCH
      else -> ReleaseType.NONE
    }
  }
  return releaseType
}

enum class ReleaseType {
  MAJOR, MINOR, PATCH, NONE
}
