package com.twilio.passkeys.extensions

import platform.Foundation.NSData
import platform.Foundation.create

private const val BASE64_BLOCK_SIZE = 4

/**
 * Decodes a Base64URL string (padded or unpadded) into [NSData].
 *
 * Foundation only understands the standard Base64 alphabet, so `-` and `_` are mapped
 * back to `+` and `/` and the padding is restored before decoding.
 *
 * @receiver The Base64URL-encoded string to be decoded.
 * @return The decoded data, or null if the string is not valid Base64URL.
 */
internal fun String.b64UrlToNSData(): NSData? {
  val base64 = this.replace('-', '+').replace('_', '/').trimEnd('=')
  val padding = (BASE64_BLOCK_SIZE - base64.length % BASE64_BLOCK_SIZE) % BASE64_BLOCK_SIZE
  return NSData.create(base64EncodedString = base64 + "=".repeat(padding), options = 0u)
}
