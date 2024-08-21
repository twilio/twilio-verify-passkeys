require 'net/http'
require 'json'
require 'uri'

begin
  args = ARGV
  if args.size != 6
   raise ArgumentError, "Invalid usage: use `ruby create_github_release.rb {{repo_owner}} {{repo_name}} {{access_token}} {{tag_name}} {{release_name}} {{release_notes}}`"
  end
  repo_owner = args[0]
  repo_name = args[1]
  access_token = args[2]
  tag_name = args[3]
  release_name = args[4]
  release_notes = args[5]

  puts release_notes
  github_api_url = "https://api.github.com/repos/#{repo_owner}/#{repo_name}/releases"

  release_data = {
    "tag_name" => tag_name,
    "target_commitish" => "main",
    "name" => release_name,
    "body" => release_notes,
    "draft" => false,
    "prerelease" => false
  }

  headers = {
    'Authorization' => "token #{access_token}",
    'Accept' => 'application/vnd.github.v3+json',
    'Content-Type' => 'application/json'
  }

  uri = URI.parse(github_api_url)

  # Create the HTTP request
  http = Net::HTTP.new(uri.host, uri.port)
  http.use_ssl = true

  request = Net::HTTP::Post.new(uri.path, headers)
  request.body = release_data.to_json

  # Make the request
  response = http.request(request)

  # Check the response
  if response.code.to_i == 201
    puts "Release created successfully"
    puts "Release URL: #{JSON.parse(response.body)['html_url']}"
  else
    puts "Failed to create release"
    puts "Response: #{response.body}"
  end
end
