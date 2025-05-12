require 'open3'

module GitTools
  # Function to get the last tag matching the platform-specific suffix
  def self.get_last_tag(platform_tag_suffix)
    command = "git describe --tags --match=*#{platform_tag_suffix}"
    output, status = Open3.capture2(command)

    if status.success?
      parts = output.strip.split("-")
      sliced_parts = parts[0...-2]
      return sliced_parts.join("-")
    else
      return ""
    end
  end

  def self.get_previous_version_tag(platform_tag_suffix)
    command = "git tag --list \"*#{platform_tag_suffix}\" --sort=-v:refname"
    output, status = Open3.capture2(command)

    if status.success?
      tags = output.strip.split("\n")

      return tags[1] if tags.length > 1 # Second-to-last tag
    end

    nil # Return nil if there's no second-to-last tag
  end

  # Function to get commit history from a given tag to HEAD
  def self.get_commit_history(from_tag)
    if from_tag.strip.empty?
      command = "git log --pretty=format:%s"
    else
      command = "git log --pretty=format:%s #{from_tag}..HEAD"
    end

    output, status = Open3.capture2(command)

    if status.success?
      return output.split("\n")
    else
      raise "Failed to get commit history: #{output}"
    end
  end
end
