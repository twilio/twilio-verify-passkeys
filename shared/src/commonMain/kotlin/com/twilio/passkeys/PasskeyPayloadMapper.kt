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

package com.twilio.passkeys

import com.twilio.passkeys.exception.INVALID_JSON_PAYLOAD_ERROR
import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.exception.UNKNOWN_ERROR
import com.twilio.passkeys.extensions.b64Decode
import com.twilio.passkeys.extensions.b64Encode
import com.twilio.passkeys.models.AuthenticatePasskeyDto
import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.AuthenticatePasskeyResponse
import com.twilio.passkeys.models.CreatePasskeyDto
import com.twilio.passkeys.models.CreatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
internal object PasskeyPayloadMapper {
  @OptIn(ExperimentalSerializationApi::class)
  private val json =
    Json {
      ignoreUnknownKeys = true
      explicitNulls = false
    }

  fun mapToPasskeyCreationPayload(challengePayload: String): CreatePasskeyRequest {
    val registerPasskeyRequest = json.decodeFromString<CreatePasskeyRequest>(challengePayload)
    registerPasskeyRequest.apply {
      challenge = challenge.b64Decode().b64Encode()
      user.id = user.id.b64Decode().b64Encode()
    }
    return registerPasskeyRequest
  }

  fun mapToPasskeyAuthenticationPayload(challengePayload: String): AuthenticatePasskeyRequest {
    val authenticatePasskeyRequest =
      json.decodeFromString<AuthenticatePasskeyRequest>(challengePayload)
    authenticatePasskeyRequest.publicKey.apply {
      challenge = challenge.b64Decode().b64Encode()
    }
    return authenticatePasskeyRequest
  }


  fun mapToPasskeyCreationResponse(registrationResultJson: String): CreatePasskeyResponse {
    val createPasskeyDto = json.decodeFromString<CreatePasskeyDto>(registrationResultJson)
    return CreatePasskeyResponse(
      id = createPasskeyDto.id,
      rawId = createPasskeyDto.rawId,
      authenticatorAttachment = createPasskeyDto.authenticatorAttachment,
      type = createPasskeyDto.type,
      attestationObject = createPasskeyDto.response.attestationObject,
      clientDataJSON = createPasskeyDto.response.clientDataJSON,
      transports = createPasskeyDto.response.transports,
    )
  }


  fun mapToAuthenticatePasskeyResponse(authenticatePasskeyResultJson: String): AuthenticatePasskeyResponse {
    val authenticatePasskeyDto =
      json.decodeFromString<AuthenticatePasskeyDto>(authenticatePasskeyResultJson)
    return AuthenticatePasskeyResponse(
      id = authenticatePasskeyDto.id,
      rawId = authenticatePasskeyDto.rawId,
      authenticatorAttachment = authenticatePasskeyDto.authenticatorAttachment,
      type = authenticatePasskeyDto.type,
      clientDataJSON = authenticatePasskeyDto.response.clientDataJSON,
      authenticatorData = authenticatePasskeyDto.response.authenticatorData,
      signature = authenticatePasskeyDto.response.signature,
      userHandle = authenticatePasskeyDto.response.userHandle,
    )
  }

  /*
  fun mapException(e: Exception): TwilioException {
    return when (e) {
      is SerializationException, is IllegalArgumentException, is IndexOutOfBoundsException ->
        TwilioException(
          INVALID_JSON_PAYLOAD_ERROR,
          e.message.toString(),
        )
      else -> TwilioException(UNKNOWN_ERROR, e.message.toString())
    }
  }
   */
}
