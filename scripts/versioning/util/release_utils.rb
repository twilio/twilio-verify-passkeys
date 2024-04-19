require_relative '../model/release_platform'
require_relative '../model/version_bump_type'

module ReleaseUtils
  def self.determine_release_type(commits, platform)

    # Method to determine if there are any breaking changes in the commits
    def self.is_breaking_change(commits, platform)
      regex = if platform == ReleasePlatform[:IOS]
                /^(?i)(feat|fix|refactor)(\[[^\]]*ios[^\]]*\])?(\(.*\))?!:.+/
              else
                /^(?i)(feat|fix|refactor)(\[[^\]]+\])?(\(.*\))?!:.+/
              end
      commits.any? { |commit| commit.match?(regex) }
    end

    # Method to determine if there are any features in the commits
    def self.is_feature(commits, platform)
      regex = if platform == ReleasePlatform[:IOS]
                 /^(?i)(feat)(\[[^\]]*ios[^\]]*\])?(\(.*\))?:.+/
              else
                 /^(?i)(feat)(\[[^\]]+\])?(\(.*\))?:.+/
              end
      commits.any? { |commit| commit.match?(regex) }
    end

    # Method to determine if there are any fixes in the commits
    def self.is_fix(commits, platform)
      regex = if platform == ReleasePlatform[:IOS]
                /^(?i)(fix)(\[[^\]]*ios[^\]]*\])?(\(.*\))?:.+/
              else
                /^(?i)(fix)(\[[^\]]+\])?(\(.*\))?:.+/
              end
      commits.any? { |commit| commit.match?(regex) }
    end

    is_breaking_change = is_breaking_change(commits, platform)
    is_feature = is_feature(commits, platform)
    is_fix = is_fix(commits, platform)

    if is_breaking_change
      VersionBumpType[:MAJOR]
    elsif is_feature
      VersionBumpType[:MINOR]
    elsif is_fix
      VersionBumpType[:PATCH]
    else
      VersionBumpType[:NONE]
    end
  end
end
