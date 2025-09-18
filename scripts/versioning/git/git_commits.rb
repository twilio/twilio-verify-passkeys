require_relative '../model/release_platform'
require_relative 'git_tools'

module GitCommits
  def self.get_commits(platform)
    platform_tag_suffix = platform[:tag_suffix]

    # Check if platform_tag_suffix is present and valid
    unless platform_tag_suffix
      raise ArgumentError, "Platform tag_suffix is missing or invalid"
    end

    last_tag = GitTools.get_last_tag(platform_tag_suffix)
    commit_history = GitTools.get_commit_history(last_tag)
    return commit_history
  end
end
