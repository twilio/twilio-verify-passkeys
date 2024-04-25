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

package com.twilio.passkeys.exception

import kotlin.js.JsExport
import kotlin.js.JsName

internal const val INVALID_JSON_PAYLOAD_ERROR = "INVALID_JSON_PAYLOAD"
internal const val UNKNOWN_ERROR = "UNKNOWN"

@JsExport
data class TwilioException(
  val message: String,
) {
  @JsName("withType")
  constructor(type: String, message: String) : this("$type: $message")
}
