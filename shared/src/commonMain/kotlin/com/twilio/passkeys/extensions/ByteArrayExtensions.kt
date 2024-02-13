package com.twilio.passkeys.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.b64Encode(): String {
  return Base64.UrlSafe.encode(this)
}
