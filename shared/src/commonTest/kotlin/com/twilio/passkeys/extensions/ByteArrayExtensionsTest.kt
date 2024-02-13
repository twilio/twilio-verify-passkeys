package com.twilio.passkeys.extensions

import com.twilio.passkeys.extensions.b64Encode
import kotlin.test.Test
import kotlin.test.assertEquals

class ByteArrayExtensionsTest {
  @Test
  fun `Base64 url safe encode`() {
    val encode = "hi".encodeToByteArray().b64Encode()
    assertEquals("aGk=", encode)
  }
}
