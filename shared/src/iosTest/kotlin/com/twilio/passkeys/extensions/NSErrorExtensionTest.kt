package com.twilio.passkeys.extensions

import com.twilio.passkeys.exception.TwilioException
import platform.Foundation.NSError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NSErrorExtensionTest {
  @Test
  fun `NSError maps to UserCanceledException`() {
    val error = NSError(domain = "Test", code = PASSKEY_CANCELED_ERROR_CODE, userInfo = null)
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.UserCanceledException)
  }

  @Test
  fun `NSError maps to DomException`() {
    val error = NSError(domain = "Test", code = PASSKEY_INVALID_RESPONSE_ERROR_CODE, userInfo = null)
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.DomException)
    assertEquals(result.message, DOM_MESSAGE)
  }

  @Test
  fun `NSError maps to UnsupportedException`() {
    val error = NSError(domain = "Test", code = PASSKEY_UNSUPPORTED_ERROR_CODE, userInfo = null)
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.UnsupportedException)
  }

  @Test
  fun `NSError with NOT_HANDLED maps to GeneralException`() {
    val error = NSError(domain = "Test", code = PASSKEY_NOT_HANDLED_ERROR_CODE, userInfo = null)
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.GeneralException)
  }

  @Test
  fun `NSError with NOT_INTERACTIVE maps to GeneralException`() {
    val error = NSError(domain = "Test", code = PASSKEY_NOT_INTERACTIVE_ERROR_CODE, userInfo = null)
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.GeneralException)
  }

  @Test
  fun `NSError with FAILED maps to GeneralException`() {
    val error = NSError(domain = "Test", code = PASSKEY_FAILED_ERROR_CODE, userInfo = null)
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.GeneralException)
  }

  @Test
  fun `NSError with unknown code maps to GeneralException`() {
    val error = NSError(domain = "Test", code = 9999, userInfo = null) // Use a code that is not defined
    val result = error.toTwilioException()
    assertTrue(result is TwilioException.GeneralException)
  }
}
