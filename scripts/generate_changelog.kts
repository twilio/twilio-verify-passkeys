#!/usr/bin/env kotlin
import java.io.BufferedReader
import kotlin.system.exitProcess

if (args.size != 2) {
  println("Usage ./requires_release.kts {{from_tag_string}} {{check_only_ios_boolean}}")
  exitProcess(400)
}

val gitTag = args[0]
val checkOnlyIOS = args[1].toBooleanStrict()

println(generateChangelog(gitTag, checkOnlyIOS))

fun generateChangelog(fromTag: String, checkOnlyIOS: Boolean): String {
  val commits = getCommitHistory(fromTag)
  println(commits)

  val changelog = StringBuilder("# Changelog\n\n")

  val breakingChanges = mutableListOf<String>()
  val categorizedCommits = commits
    .filter { it.matches(Regex("^(feat|fix|docs|style|refactor|perf|test|chore)!?(\\(.+\\))?: .+", RegexOption.DOT_MATCHES_ALL)) }
    .groupBy { commit ->
      Regex("^(feat|fix|docs|style|refactor|perf|test|chore)(\\(.+\\))?!?:")
        .find(commit)?.value?.trim()?.trimEnd('!', ':') ?: "others"
    }

  println("categorizedCommits $categorizedCommits")
  categorizedCommits.forEach { (type, messages) ->
    changelog.append("## ${type.replaceFirstChar { it.uppercase() }}\n")
    messages.distinct().forEach { message ->
      val description = message.lineSequence().first().substringAfter(": ").capitalize()
      val scope = Regex("\\((.*?)\\)").find(message)?.groupValues?.get(1)?.let { "($it)" } ?: ""
      val isBreaking = message.contains("BREAKING CHANGE:") || message.contains("!")

      if (isBreaking) breakingChanges.add("$description $scope")

      changelog.append("- $description $scope\n")
      if (isBreaking) {
        val breakingChangeDescription = message.lineSequence()
          .firstOrNull { it.startsWith("BREAKING CHANGE:") }
          ?.substringAfter("BREAKING CHANGE:")
          ?.trim()
          ?: "Refer to commit for details."
        changelog.append("  - **BREAKING CHANGE**: $breakingChangeDescription\n")
      }
    }
    changelog.append("\n")
  }

  if (breakingChanges.isNotEmpty()) {
    changelog.insertAfter("# Changelog\n\n", "## Breaking Changes\n\n${breakingChanges.joinToString("\n") { "- $it" }}\n\n")
  }

  return changelog.toString()
}

fun StringBuilder.insertAfter(search: String, insertion: String): StringBuilder {
  val index = this.indexOf(search)
  if (index != -1) {
    this.insert(index + search.length, insertion)
  }
  return this
}

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }


fun getCommitHistory(fromTag: String): List<String> {
  val process: Process = ProcessBuilder("git", "log", "--pretty=format:%s", "$fromTag..HEAD").start()
  val output = process.inputStream.bufferedReader().readLines()
  process.waitFor()

  return output
}


fun BufferedReader.readToEnd() = this.lineSequence().joinToString("\n")
