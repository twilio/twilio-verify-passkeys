require_relative 'gradle_property_utils'

property_name = ARGV[0]

if property_name.nil?
  puts "Usage: ruby get_gradle_property.rb <property_name>"
  exit 1
end

puts GradlePropertyUtils.get_property(property_name)
