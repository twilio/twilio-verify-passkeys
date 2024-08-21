module BooleanUtils
  def self.to_boolean_strict(value)
    case value.downcase
    when 'true'
      true
    when 'false'
      false
    else
      raise ArgumentError, "Invalid value for Boolean: \"#{value}\""
    end
  end
end
