require_relative 'git_tools'
require_relative '../model/release_platform'

tag_type = ARGV[0]

if tag_type.nil?
  puts "Usage: ruby get_last_released_version.rb <tag_type>"
  puts "Available tag types: #{ReleasePlatform.keys.join(', ')}"
  exit 1
end

platform_tag_suffix = ReleasePlatform[tag_type.to_sym]&.dig(:tag_suffix)

if platform_tag_suffix.nil?
  puts "Invalid tag type. Available types: #{ReleasePlatform.keys.join(', ')}"
  exit 1
end

last_tag = GitTools.get_last_tag(platform_tag_suffix)
last_released_version = last_tag.gsub(platform_tag_suffix, "")
puts last_released_version