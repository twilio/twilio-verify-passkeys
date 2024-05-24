require_relative 'git/git_commits'
require_relative 'util/boolean_utils'
require_relative 'util/release_utils'
require_relative 'util/gradle_property_utils'
require_relative 'model/version_bump_type'

begin
  args = ARGV

  # Check if the required argument is provided
  if args.empty?
    raise ArgumentError,  "Invalid usage: use `ruby bump_kmp_sdk_version.rb {{version_bump_type}}`"
  end

  bump_type = case args[0].upcase
  when VersionBumpType[:MAJOR] then VersionBumpType[:MAJOR]
  when VersionBumpType[:MINOR] then VersionBumpType[:MINOR]
  when VersionBumpType[:PATCH] then VersionBumpType[:PATCH]
  else raise ArgumentError, "version_bump_type '#{args[0]}' is invalid. Use 'MAJOR', 'MINOR', or 'PATCH'"
  end

  property = "sdkVersionName"
  current_sdk_version = GradlePropertyUtils.get_property(property)
  current_sdk_version = current_sdk_version.split('.')

  next_major = current_sdk_version[0].to_i
  next_minor = current_sdk_version[1].to_i
  next_patch = current_sdk_version[2].to_i

  case bump_type
  when VersionBumpType[:MAJOR]
    next_major += 1
    next_minor = 0
    next_patch = 0
  when VersionBumpType[:MINOR]
    next_minor += 1
    next_patch = 0
  when VersionBumpType[:PATCH]
    next_patch += 1
  end

  next_version = "#{next_major}.#{next_minor}.#{next_patch}"
  GradlePropertyUtils.set_property(property, next_version)
  puts "New version = #{next_version}"
rescue ArgumentError => e
  raise e, e.message
end
