require 'pathname'

module GradlePropertyUtils
  def self.get_property(property_name)
    gradle_properties_file = 'gradle.properties'

    unless File.exist?(gradle_properties_file)
      raise StandardError.new("#{gradle_properties_file} file not found.")
    end

    # Read existing properties
    properties = {}

    File.open(gradle_properties_file, "r") do |file|
      file.each_line do |line|
        # Skip empty lines or lines starting with '#' (comments)
        next if line.empty? || line.start_with?('#')

        # Split each line into key and value pair
        key, value = line.strip.split('=')
        properties[key.strip] = value.strip if key && value
      end
    end

    property = properties[property_name]

    unless property
      raise StandardError.new("#{property_name} property not found in gradle.properties.")
    end

    property
  end


  def self.set_property(property_name, property_value)
      gradle_properties_file = 'gradle.properties'

      unless File.exist?(gradle_properties_file)
        raise StandardError.new("#{gradle_properties_file} file not found.")
      end

      updated_content = []

      File.readlines(gradle_properties_file).each do |line|
        if line =~ /^\s*#{property_name}\s*=\s*(.*)$/
          # Update property with new value
          updated_content << "#{property_name} = #{property_value}\n"
        else
          # Preserve other lines
          updated_content << line
        end
      end

      # Write the updated content back to the file
      File.open(gradle_properties_file, 'w') do |file|
        file.puts(updated_content.join)
      end
    end
end
