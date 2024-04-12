#!/usr/bin/env kotlin
import java.io.BufferedReader
import kotlin.system.exitProcess

if (args.size != 2) {
  println("Usage ./generate_changelog.kts {{from_tag_string}} {{platform}}")
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

println(generateChangelog(gitTag, platform))

fun generateChangelog(fromTag: String, platform: Platform): String {
  val commits = getCommitHistory(fromTag)

  val changelog = StringBuilder("# Changelog\n\n")

  val prefixPattern =
    "^(${ConventionalCommit.FEAT.prefix}|${ConventionalCommit.FIX.prefix}|${ConventionalCommit.DOCS.prefix}|${ConventionalCommit.STYLE.prefix}|${ConventionalCommit.REFACTOR.prefix}|${ConventionalCommit.PERF.prefix}|${ConventionalCommit.TEST.prefix}|${ConventionalCommit.CHORE.prefix})"

  val allTypesPattern = "!?(\\[.+\\])?(\\(.+\\))?:.+"

  val regexFilter = if (platform == Platform.IOS) {
    Regex(
      "$prefixPattern(\\[(?i)ios\\])?(?!\\[(?i)android\\])!?(\\(.+\\))?:.+",
      RegexOption.DOT_MATCHES_ALL
    )
  } else {
    Regex(
      "$prefixPattern$allTypesPattern",
      RegexOption.DOT_MATCHES_ALL
    )
  }
  val categorizedCommits = commits
    .filter {
      it.matches(regexFilter)
    }.groupBy {
      when {
        it.matches(Regex("^(${ConventionalCommit.FEAT.prefix})$allTypesPattern")) -> ConventionalCommit.FEAT
        it.matches(Regex("^(${ConventionalCommit.FIX.prefix})$allTypesPattern")) -> ConventionalCommit.FIX
        it.matches(Regex("^(${ConventionalCommit.DOCS.prefix})$allTypesPattern")) -> ConventionalCommit.DOCS
        it.matches(Regex("^(${ConventionalCommit.STYLE.prefix})$allTypesPattern")) -> ConventionalCommit.STYLE
        it.matches(Regex("^(${ConventionalCommit.REFACTOR.prefix})$allTypesPattern")) -> ConventionalCommit.REFACTOR
        it.matches(Regex("^(${ConventionalCommit.PERF.prefix})$allTypesPattern")) -> ConventionalCommit.PERF
        it.matches(Regex("^(${ConventionalCommit.TEST.prefix})$allTypesPattern")) -> ConventionalCommit.TEST
        it.matches(Regex("^(${ConventionalCommit.CHORE.prefix})$allTypesPattern")) -> ConventionalCommit.CHORE
        else -> ConventionalCommit.GENERAL_CHANGE
      }
    }

  val order = listOf(
    ConventionalCommit.FEAT,
    ConventionalCommit.FIX,
    ConventionalCommit.DOCS,
    ConventionalCommit.STYLE,
    ConventionalCommit.REFACTOR,
    ConventionalCommit.PERF,
    ConventionalCommit.TEST,
    ConventionalCommit.CHORE
  )

  val sortedCommits = order.mapNotNull { key ->
    categorizedCommits[key]?.let { key to it }
  }.toMap()

  sortedCommits.forEach { (type, messages) ->
    changelog.append("## ${type.title}\n")
    messages.distinct().forEach { message ->
      val description = message.lineSequence().first().substringAfter(": ").capitalize()

      val os = if (platform == Platform.KMP) {
        Regex("\\[(.*?)\\]").find(message)?.groupValues?.get(1)
          ?.let { if (it.lowercase() == "ios") "iOS" else if (it.lowercase() == "android") "Android" else "" } ?: ""
      } else {
        ""
      }

      val scope = Regex("\\((.*?)\\)").find(message)?.groupValues?.get(1)?.let { "(${it.capitalize()})" } ?: ""
      val fullDescription = StringBuilder("- ")
      if (scope.isNotBlank()) {
        fullDescription.append("**$scope** ")
      }
      if (os.isNotBlank()) {
        fullDescription.append("***$os*** ")
      }
      fullDescription.append(description)

      changelog.append("$fullDescription\n")
    }
  }

  return changelog.toString()
}

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun getCommitHistory(fromTag: String): List<String> {
  val process: Process = ProcessBuilder("git", "log", "--pretty=format:%s", "$fromTag..HEAD").start()
  val output = process.inputStream.bufferedReader().readLines()
  process.waitFor()

  return output
}

enum class ConventionalCommit(val title: String, val prefix: String) {
  FEAT("\uD83D\uDE80 Features", "feat"),
  FIX("\uD83D\uDC1B Bug Fixes", "fix"),
  DOCS("\uD83D\uDCDA Documentation", "docs"),
  STYLE("\uD83C\uDFA8 Styling", "style"),
  REFACTOR("\uD83D\uDE9C Refactor", "refactor"),
  PERF("⚡\uFE0F Performance", "perf"),
  TEST("\uD83E\uDDEA Testing", "test"),
  CHORE("\uD83E\uDDF9 Chore", "chore"),
  GENERAL_CHANGE("\uD83D\uDD04 Improvements", "")
}

enum class Platform {
  KMP,
  IOS
}
