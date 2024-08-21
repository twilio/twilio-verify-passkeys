require_relative 'model/conventional_commit'
require_relative 'model/platform'

module ChangelogGenerator
  # Method to parse a commit into a pair of ConventionalCommit and list of Platforms
  def self.parse_commit(commit)
    type = case
           when commit.match?(/^(?i)(#{ConventionalCommit[:FEAT][:prefix]}).*:.*/i) then ConventionalCommit[:FEAT]
           when commit.match?(/^(?i)(#{ConventionalCommit[:FIX][:prefix]}).*:.*/i) then ConventionalCommit[:FIX]
           when commit.match?(/^(?i)(#{ConventionalCommit[:DOCS][:prefix]}).*:.*/i) then ConventionalCommit[:DOCS]
           when commit.match?(/^(?i)(#{ConventionalCommit[:STYLE][:prefix]}).*:.*/i) then ConventionalCommit[:STYLE]
           when commit.match?(/^(?i)(#{ConventionalCommit[:REFACTOR][:prefix]}).*:.*/i) then ConventionalCommit[:REFACTOR]
           when commit.match?(/^(?i)(#{ConventionalCommit[:PERF][:prefix]}).*:.*/i) then ConventionalCommit[:PERF]
           when commit.match?(/^(?i)(#{ConventionalCommit[:TEST][:prefix]}).*:.*/i) then ConventionalCommit[:TEST]
           when commit.match?(/^(?i)(#{ConventionalCommit[:CHORE][:prefix]}).*:.*/i) then ConventionalCommit[:CHORE]
           else nil
           end

    platforms = commit[/(?<=\[)(.*?)(?=\])/]&.split(", ")&.map do |p|
      case p.strip.downcase
      when Platform[:ANDROID][:platform_name].downcase then Platform[:ANDROID]
      when Platform[:IOS][:platform_name].downcase then Platform[:IOS]
      when Platform[:WEB][:platform_name].downcase then Platform[:WEB]
      else nil
      end
    end || [Platform[:ANDROID], Platform[:IOS], Platform[:WEB]]

    return nil if type.nil?
    return [type, platforms]
  end

  # Method to group commits by type and platform
  def self.group_commits(commits)
    grouped_commits = {}

    commits.each do |commit|
      type, platforms = parse_commit(commit)
      next if type.nil?

      grouped_commits[type] ||= {}

      platforms.each do |platform|
        grouped_commits[type][platform] ||= []
        grouped_commits[type][platform] << commit
      end
    end

    grouped_commits
  end

  # Method to generate the changelog based on new version, commits, and platform flag
  def self.generate_changelog(new_version, commits, is_ios_only)
    changelog = "# #{new_version}\n\n"
    grouped_commits = group_commits(commits)
    order = ConventionalCommit.keys.map { |key| ConventionalCommit[key] }
    sorted_commits = order.map { |key| [key, grouped_commits[key]] }.to_h.compact

    sorted_commits.each do |type, platforms|
      if is_ios_only
        ios_platform = platforms[Platform[:IOS]]
        next unless ios_platform

        changelog << "## #{type[:title]}\n"
        ios_platform.each do |message|
          changelog << "#{generate_full_description(message)}\n"
        end
      else
        changelog << "## #{type[:title]}\n"
        platforms.each do |platform, messages|
          changelog << "### #{platform[:icon]} #{platform[:platform_name]}\n"
          messages.each { |message| changelog << "#{generate_full_description(message)}\n" }
        end
      end
    end

    changelog
  end

  # Helper method to generate a formatted description from a commit message
  def self.generate_full_description(message)
    description = message.split(': ', 2).last.capitalize
    scope = message[/\((.*?)\)/, 1]&.capitalize
    full_description = "- "
    full_description << "**#{scope}** " if scope
    full_description << description
  end
end
