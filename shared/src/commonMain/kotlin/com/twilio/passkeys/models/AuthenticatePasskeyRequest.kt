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

/**
 * Represents a request for authenticating a passkey.
 *
 * @property publicKey The public key associated with the authentication request.
 */
@Serializable
data class AuthenticatePasskeyRequest(val publicKey: AuthenticatePasskeyRequestPublicKey)

/**
 * Represents the public key information used for authenticating a passkey.
 *
 * @property challenge The challenge string used for authentication.
 * @property timeout The timeout for the authentication request.
 * @property rpId The relying party ID.
 * @property allowCredentials The list of allowed credentials for authentication.
 * @property userVerification The user verification method.
 */
@Serializable
data class AuthenticatePasskeyRequestPublicKey(
  var challenge: String,
  val timeout: Long,
  val rpId: String,
  val allowCredentials: List<KeyCredential>,
  val userVerification: String,
)
