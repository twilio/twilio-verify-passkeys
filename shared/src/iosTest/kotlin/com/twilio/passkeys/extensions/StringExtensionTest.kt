package com.twilio.passkeys.extensions

import com.twilio.passkeys.toUrlSafeString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringExtensionTest {
  @Test
  fun `Base64URL challenge with one url-safe character decodes to 32 bytes`() {
    assertEquals(32UL, "RdvkeBjVj1eKgp9mdCrSqbtLKaTamiyV_6KJqYheP8U".b64UrlToNSData()?.length)
  }

  @Test
  fun `Base64URL challenge with two url-safe characters decodes to 32 bytes`() {
    assertEquals(32UL, "hLh-ZESOaTgl1bMWFHgigQTevbt_GF2B9675fa2Qm4Y".b64UrlToNSData()?.length)
  }

  @Test
  fun `Base64URL challenge with three url-safe characters decodes to 32 bytes`() {
    assertEquals(32UL, "ex6mCZglxja-SYwVm5RI4islKourA_leLB_hDckhVb0".b64UrlToNSData()?.length)
  }

  @Test
  fun `Padded Base64URL challenge decodes to 32 bytes`() {
    assertEquals(32UL, "ex6mCZglxja-SYwVm5RI4islKourA_leLB_hDckhVb0=".b64UrlToNSData()?.length)
  }

  @Test
  fun `Decoded bytes round trip to the same Base64URL string`() {
    val challenge = "hLh-ZESOaTgl1bMWFHgigQTevbt_GF2B9675fa2Qm4Y"
    assertEquals(challenge, challenge.b64UrlToNSData()?.toUrlSafeString())
  }

  @Test
  fun `Invalid Base64URL string returns null`() {
    assertNull("not base64!".b64UrlToNSData())
  }
}
