require_relative 'git/git_commits'
require_relative 'util/boolean_utils'
require_relative 'util/release_utils'
require_relative 'model/release_platform'

begin
  args = ARGV

  # Check if the required argument is provided
  if !args[0]
    raise ArgumentError, "Invalid usage: use `ruby require_release.rb {{is_ios_only}}``"
  end
  # Convert the argument to a boolean value
  is_ios_only = BooleanUtils.to_boolean_strict(args[0])
  # Determine the release platform based on the boolean argument
  platform = is_ios_only ? ReleasePlatform[:IOS] : ReleasePlatform[:KMP]
  # Retrieve commits based on the specified platform
  commits = GitCommits.get_commits(platform)
  

  puts ReleaseUtils.determine_release_type(commits, platform)
rescue ArgumentError => e
  puts e.message
end
