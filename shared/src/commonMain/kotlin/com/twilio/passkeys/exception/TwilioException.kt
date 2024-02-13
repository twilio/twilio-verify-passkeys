package com.twilio.passkeys.exception

internal const val INVALID_JSON_PAYLOAD_ERROR = "INVALID_JSON_PAYLOAD"
internal const val UNKNOWN_ERROR = "UNKNOWN"

data class TwilioException(
  val message: String,
) {
  constructor(type: String, message: String) : this("$type: $message")
}
