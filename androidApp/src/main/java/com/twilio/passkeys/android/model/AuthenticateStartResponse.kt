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

package com.twilio.passkeys.android.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateStartResponse(
  val publicKey: PublicKeyCredential,
)

@Serializable
data class PublicKeyCredential(
  val challenge: String,
  val timeout: Int,
  val rpId: String,
  val allowCredentials: List<AllowCredential>,
  val userVerification: String,
)

@Serializable
data class AllowCredential(
  val id: String,
  val type: String,
  val transports: List<String>,
)
