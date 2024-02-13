package com.twilio.passkeys.exception

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.exception.UNKNOWN_ERROR
import kotlin.test.Test
import kotlin.test.assertEquals

class TwilioExceptionTest {
  @Test
  fun `Twilio exception with message`() {
    val message = "This is the exception message"
    val twilioException = TwilioException(message = message)

    assertEquals(twilioException.message, message)
  }

  @Test
  fun `Twilio exception with type and message`() {
    val type = UNKNOWN_ERROR
    val message = "This is the exception message"
    val twilioException = TwilioException(type = type, message = message)

    assertEquals(twilioException.message, "$type: $message")
  }
}
