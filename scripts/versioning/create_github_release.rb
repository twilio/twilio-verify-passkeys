require 'faraday'
require 'json'

def create_github_release(repo_owner, repo_name, access_token, tag_name, release_name, release_notes, file_path=nil)
  github_api_url = "https://api.github.com/repos/#{repo_owner}/#{repo_name}/releases"

  headers = {
    'Authorization' => "token #{access_token}",
    'Accept' => 'application/vnd.github.v3+json',
    'Content-Type' => 'application/json'
  }

  release_data = {
    "tag_name" => tag_name,
    "target_commitish" => "main",
    "name" => release_name,
    "body" => release_notes,
    "draft" => false,
    "prerelease" => false
  }

  response = Faraday.post(github_api_url, release_data.to_json, headers)

  if response.status == 201
    puts "Release URL: #{JSON.parse(response.body)}"
    release_url = JSON.parse(response.body)['upload_url'].gsub("{?name,label}", "?name=#{File.basename(file_path)}")
    if file_path
      upload_asset(release_url, file_path, access_token)
    end
  else
    puts "Failed to create release"
    puts "Response: #{response.body}"
  end
end

def upload_asset(upload_url, file_path, access_token)
  file = File.open(file_path)
  content_length = file.size

  headers = {
    'Authorization' => "token #{access_token}",
    'Content-Type' => 'application/zip',
    'Content-Length' => content_length.to_s
  }

  response = Faraday.post(upload_url, file, headers)

  if response.status == 201
    puts "Asset uploaded successfully"
  else
    puts "Failed to upload asset"
    puts "Response: #{response.body}"
  end
end

# Example usage with optional file upload
begin
  args = ARGV
  if args.size < 6 || args.size > 7
    raise ArgumentError, "Invalid usage: use `ruby create_github_release.rb {{repo_owner}} {{repo_name}} {{access_token}} {{tag_name}} {{release_name}} {{release_notes}} [{{file_path}}]` (optional file)"
  end
  repo_owner = args[0]
  repo_name = args[1]
  access_token = args[2]
  tag_name = args[3]
  release_name = args[4]
  release_notes = args[5]
  file_path = args[6] if args.size == 7

  create_github_release(repo_owner, repo_name, access_token, tag_name, release_name, release_notes, file_path)
end
