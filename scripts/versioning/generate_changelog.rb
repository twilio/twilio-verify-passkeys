require_relative 'changelog_generator'
require_relative 'git/git_commits'
require_relative 'util/boolean_utils'
require_relative 'model/release_platform'
require_relative 'util/gradle_property_utils'
begin
  args = ARGV

  # Check if the required argument is provided
  if args.size != 1
      raise ArgumentError, "Invalid usage: use `ruby generate_changelog.rb {{is_ios_only}}``"
  end
  # Convert the argument to a boolean value
  is_ios_only = BooleanUtils.to_boolean_strict(args[0])
  # Determine the release platform based on the boolean argument
  platform = is_ios_only ? ReleasePlatform[:IOS] : ReleasePlatform[:KMP]
  # Retrieve commits based on the specified platform
  commits = GitCommits.get_commits(platform)

  sdk_version = GradlePropertyUtils.get_property("kmpSdkVersionName")
  puts ChangelogGenerator.generate_changelog(sdk_version, commits, is_ios_only)
rescue ArgumentError => e
  puts e.message
end
