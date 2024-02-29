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

package com.twilio.passkeys.models

import kotlinx.serialization.Serializable

data class AuthenticatePasskeyResponse(
  val id: String,
  val rawId: String,
  val authenticatorAttachment: String,
  val type: String,
  val clientDataJSON: String,
  val authenticatorData: String?,
  val signature: String?,
  val userHandle: String?,
)

@Serializable
data class AuthenticatePasskeyDto(
  val rawId: String,
  val id: String,
  val authenticatorAttachment: String,
  val type: String,
  val response: AuthenticatePasskeyResponseDto,
)

@Serializable
data class AuthenticatePasskeyResponseDto(
  val clientDataJSON: String,
  val authenticatorData: String,
  val signature: String,
  val userHandle: String,
)
