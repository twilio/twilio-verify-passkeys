#!/usr/bin/env kotlin
import java.io.BufferedReader
import kotlin.system.exitProcess

if (args.size != 2) {
  println("Usage ./generate_changelog.kts {{from_tag_string}} {{changelog_for}}")
  exitProcess(400)
}

val gitTag = args[0]
val changelogType = if (args[1] == "KMP") {
  ChangelogType.KMP
} else if (args[1] == "iOS") {
  ChangelogType.IOS
} else {
  println("Bad usage: {{changelog_for}} parameter accepts KMP or iOS")
  exitProcess (400)
}

println(generateChangelog(gitTag, changelogType))

fun generateChangelog(fromTag: String, changelogType: ChangelogType): String {
  val commits = getCommitHistory(fromTag)
//  println(commits)

  val changelog = StringBuilder("# Changelog\n\n")

  val regexFilter = if (changelogType == ChangelogType.IOS) {
    Regex(
      "^(${ConventionalCommit.FEAT.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.FIX.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.DOCS.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.STYLE.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.REFACTOR.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.PERF.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.TEST.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\])|${ConventionalCommit.CHORE.prefix}(\\[(?i)ios\\])?(?!\\[(?i)android\\]))!?(\\(.+\\))?: .+",
      RegexOption.DOT_MATCHES_ALL
    )
  } else {
    Regex(
      "^(${ConventionalCommit.FEAT.prefix}|${ConventionalCommit.FIX.prefix}|${ConventionalCommit.DOCS.prefix}|${ConventionalCommit.STYLE.prefix}|${ConventionalCommit.REFACTOR.prefix}|${ConventionalCommit.PERF.prefix}|${ConventionalCommit.TEST.prefix}|${ConventionalCommit.CHORE.prefix})!?(\\(.+\\))?: .+",
      RegexOption.DOT_MATCHES_ALL
    )
  }
  val categorizedCommits = commits
    .filter {
      it.matches(regexFilter)
    }.groupBy {
      when {
        it.matches(Regex("^(${ConventionalCommit.FEAT.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.FEAT
        it.matches(Regex("^(${ConventionalCommit.FIX.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.FIX
        it.matches(Regex("^(${ConventionalCommit.DOCS.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.DOCS
        it.matches(Regex("^(${ConventionalCommit.STYLE.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.STYLE
        it.matches(Regex("^(${ConventionalCommit.REFACTOR.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.REFACTOR
        it.matches(Regex("^(${ConventionalCommit.PERF.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.PERF
        it.matches(Regex("^(${ConventionalCommit.TEST.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.TEST
        it.matches(Regex("^(${ConventionalCommit.CHORE.prefix})!?(\\(.+\\))?: .+")) -> ConventionalCommit.CHORE
        else -> ConventionalCommit.GENERAL_CHANGE
      }
    }
//  println("categorizedCommits $categorizedCommits")
  categorizedCommits.forEach { (type, messages) ->
    changelog.append("## ${type.title}\n")
    messages.distinct().forEach { message ->
      val description = message.lineSequence().first().substringAfter(": ").capitalize()
      val scope = Regex("\\((.*?)\\)").find(message)?.groupValues?.get(1)?.let { "(${it.capitalize()})" } ?: ""

      val fullDescription = StringBuilder("- ")
      if (scope.isNotBlank()) {
        fullDescription.append("**$scope** ")
      }
      fullDescription.append(description)

      changelog.append("$fullDescription\n")
    }
    changelog.append("\n")
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

enum class ChangelogType {
  KMP,
  IOS
}
