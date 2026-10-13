source "https://rubygems.org"

gem "abbrev"
gem "fastlane", ">= 2.228.0"

gem "danger", ">= 9.4.3"
gem "danger-shroud", ">= 0.0.7"
gem "danger-ktlint", ">= 0.0.9"
gem "danger-kotlin_detekt", ">= 0.0.3"
gem "faraday-retry"
gem "oga", ">= 3.4"

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
