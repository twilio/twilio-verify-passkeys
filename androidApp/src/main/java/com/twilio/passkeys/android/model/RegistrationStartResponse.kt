/*
 * Copyright © 2024 Twilio.
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
data class RegistrationStartResponse(
  val rp: RP,
  val user: User,
  val challenge: String,
  val pubKeyCredParams: List<PublicKeyCredentialParam>,
  val timeout: Int,
  val excludeCredentials: List<ExcludeCredential>,
  val authenticatorSelection: AuthenticatorSelection,
  val attestation: String,
)

@Serializable
data class RP(
  val id: String,
  val name: String,
)

@Serializable
data class User(
  val id: String,
  val name: String,
  val displayName: String?,
)

@Serializable
data class PublicKeyCredentialParam(
  val type: String,
  val alg: Int,
)

@Serializable
data class ExcludeCredential(
  val id: String,
  val type: String,
  val transports: List<String>,
)

@Serializable
data class AuthenticatorSelection(
  val authenticatorAttachment: String,
  val requireResidentKey: Boolean,
  val residentKey: String,
  val userVerification: String,
)
