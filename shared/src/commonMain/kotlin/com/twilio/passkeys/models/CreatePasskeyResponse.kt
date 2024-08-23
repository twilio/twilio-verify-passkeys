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
 * Represents the response for creating a passkey.
 *
 * @property id The identifier of the created passkey.
 * @property rawId The raw identifier of the created passkey.
 * @property authenticatorAttachment The type of authenticator attachment used.
 * @property type The type of the passkey.
 * @property attestationObject The attestation object associated with the passkey creation.
 * @property clientDataJSON The client data JSON associated with the passkey creation.
 * @property transports The list of transport methods supported by the passkey.
 */
data class CreatePasskeyResponse(
  val id: String,
  val rawId: String,
  val authenticatorAttachment: String,
  val type: String,
  val attestationObject: String,
  val clientDataJSON: String,
  val transports: List<String>,
)

/**
 * Represents the data transfer object (DTO) for creating a passkey.
 *
 * @property id The identifier of the passkey.
 * @property rawId The raw identifier of the passkey.
 * @property authenticatorAttachment The type of authenticator attachment used.
 * @property type The type of the passkey.
 * @property response The response containing attestation object, client data JSON, and transports.
 */
@Serializable
internal data class CreatePasskeyDto(
  val id: String,
  val rawId: String,
  val authenticatorAttachment: String,
  val type: String,
  val response: CreatePasskeyResponseDto,
)

/**
 * Represents the data transfer object (DTO) for the response of creating a passkey.
 *
 * @property attestationObject The attestation object associated with the passkey creation.
 * @property clientDataJSON The client data JSON associated with the passkey creation.
 * @property transports The list of transport methods supported by the passkey.
 */
@Serializable
internal data class CreatePasskeyResponseDto(
  val attestationObject: String,
  val clientDataJSON: String,
  val transports: List<String>,
)
