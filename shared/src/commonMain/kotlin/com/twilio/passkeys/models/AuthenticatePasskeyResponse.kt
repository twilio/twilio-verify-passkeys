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
 * Represents the response from authenticating a passkey.
 *
 * @property id The identifier of the authenticated passkey.
 * @property rawId The raw identifier of the authenticated passkey.
 * @property authenticatorAttachment The type of authenticator attachment used.
 * @property type The type of the passkey.
 * @property clientDataJSON The client data JSON associated with the passkey authentication.
 * @property authenticatorData The authenticator data associated with the passkey authentication.
 * @property signature The signature generated during passkey authentication.
 * @property userHandle The handle of the user associated with the passkey authentication.
 */
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

/**
 * Represents the data transfer object (DTO) for authenticating a passkey.
 *
 * @property rawId The raw identifier of the authenticated passkey.
 * @property id The identifier of the authenticated passkey.
 * @property authenticatorAttachment The type of authenticator attachment used.
 * @property type The type of the passkey.
 * @property response The response containing client data JSON, authenticator data, signature, and user handle.
 */
@Serializable
internal data class AuthenticatePasskeyDto(
  val rawId: String,
  val id: String,
  val authenticatorAttachment: String,
  val type: String,
  val response: AuthenticatePasskeyResponseDto,
)

/**
 * Represents the data transfer object (DTO) for the response of authenticating a passkey.
 *
 * @property clientDataJSON The client data JSON associated with the passkey authentication.
 * @property authenticatorData The authenticator data associated with the passkey authentication.
 * @property signature The signature generated during passkey authentication.
 * @property userHandle The handle of the user associated with the passkey authentication.
 */
@Serializable
internal data class AuthenticatePasskeyResponseDto(
  val clientDataJSON: String,
  val authenticatorData: String,
  val signature: String,
  val userHandle: String,
)
