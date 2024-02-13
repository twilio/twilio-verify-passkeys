package com.twilio.passkeys.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun String.b64Decode(): ByteArray {
  return Base64.UrlSafe.decode(this)
}
