package com.twilio.passkeys.extensions

import com.twilio.passkeys.extensions.b64Decode
import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtensionsTest {
  @Test
  fun `Base64 url safe decode`() {
    val decode = "SGVsbG8gV29ybGQh".b64Decode()
    assertEquals("Hello World!", decode.decodeToString().toCharArray().concatToString())
  }
}
