/*
 * Copyright © 2024 Twilio Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twilio.passkeys.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Decodes the Base64-encoded string using URL-safe decoding into a byte array.
 *
 * @receiver The Base64-encoded string to be decoded.
 * @return The decoded byte array.
 */
@OptIn(ExperimentalEncodingApi::class)
internal fun String.b64Decode(): ByteArray {
  return Base64.UrlSafe.decode(this.padBase64())
}

private const val BASE64_BLOCK_SIZE = 4
private const val NO_PADDING_NEEDED = 0

// Helper function to pad the Base64 string if needed
private fun String.padBase64(): String {
  val missingPadding = this.length % BASE64_BLOCK_SIZE
  return if (missingPadding == NO_PADDING_NEEDED) this else this + "=".repeat(BASE64_BLOCK_SIZE - missingPadding)
}
