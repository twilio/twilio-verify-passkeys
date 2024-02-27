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

package com.twilio.passkeys

import com.twilio.passkeys.exception.INVALID_JSON_PAYLOAD_ERROR
import com.twilio.passkeys.exception.UNKNOWN_ERROR
import com.twilio.passkeys.extensions.b64Decode
import com.twilio.passkeys.extensions.b64Encode
import com.twilio.passkeys.models.KeyCredential
import com.twilio.passkeys.PasskeyPayloadMapper
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals

class PasskeyPayloadMapperTest {
  private val passkeyPayloadMapper = PasskeyPayloadMapper

  @Test
  fun `Map to passkey creation payload`() {
    val rpId = "example.com"
    val rpName = "Example"
    val userId = "WUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0NjA3YjIxOTEyYg"
    val userName = "user1"
    val userDisplayName = "User One"
    val challenge = "WUYwNDhkMWE3ZWMzYTJhNjk3MDA1OWMyNzY2YmJjN2UwZg"
    val pubKeyCredType = "public-key"
    val pubKeyCredAlg = -7
    val timeout = 600000L
    val excludeCredential =
      KeyCredential(
        id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ",
        type = "public-key",
        listOf("internal", "hybrid"),
      )
    val authenticatorSelectionAuthenticatorAttachment = "platform"
    val authenticatorSelectionRequireResidentKey = false
    val authenticatorSelectionResidentKey = "preferred"
    val authenticatorSelectionUserVerification = "preferred"
    val attestation = "none"
    val payload =
      """
      {
        "rp": {
          "id": "$rpId",
          "name": "$rpName"
        },
        "user": {
          "id": "$userId",
          "name": "$userName",
          "displayName": "$userDisplayName"
        },
        "challenge": "$challenge",
        "pubKeyCredParams": [
          {
            "type": "$pubKeyCredType",
            "alg": $pubKeyCredAlg
          }
        ],
        "timeout": $timeout,
        "excludeCredentials": [
          {
              "id": "${excludeCredential.id}",
              "type": "${excludeCredential.type}",
              "transports": [
                  "${excludeCredential.transports[0]}",
                  "${excludeCredential.transports[1]}"
              ]
          }
        ],
        "authenticatorSelection": {
          "authenticatorAttachment": "$authenticatorSelectionAuthenticatorAttachment",
          "requireResidentKey": $authenticatorSelectionRequireResidentKey,
          "residentKey": "$authenticatorSelectionResidentKey",
          "userVerification": "$authenticatorSelectionUserVerification"
        },
        "attestation": "$attestation"
      }
      """.trimIndent()

    val registerPasskeyRequestPublicKey =
      passkeyPayloadMapper.mapToPasskeyCreationPayload(payload)

    assertEquals(rpId, registerPasskeyRequestPublicKey.rp.id)
    assertEquals(rpName, registerPasskeyRequestPublicKey.rp.name)
    assertEquals(userId.b64Decode().b64Encode(), registerPasskeyRequestPublicKey.user.id)
    assertEquals(userName, registerPasskeyRequestPublicKey.user.name)
    assertEquals(userDisplayName, registerPasskeyRequestPublicKey.user.displayName)
    assertEquals(challenge.b64Decode().b64Encode(), registerPasskeyRequestPublicKey.challenge)
    assertEquals(pubKeyCredType, registerPasskeyRequestPublicKey.pubKeyCredParams.first().type)
    assertEquals(pubKeyCredAlg, registerPasskeyRequestPublicKey.pubKeyCredParams.first().alg)
    assertEquals(timeout, registerPasskeyRequestPublicKey.timeout)
    assertEquals(
      excludeCredential.id,
      registerPasskeyRequestPublicKey.excludeCredentials!!.first().id,
    )
    assertEquals(
      excludeCredential.type,
      registerPasskeyRequestPublicKey.excludeCredentials!!.first().type,
    )
    assertEquals(
      excludeCredential.transports,
      registerPasskeyRequestPublicKey.excludeCredentials!!.first().transports,
    )
    assertEquals(
      authenticatorSelectionAuthenticatorAttachment,
      registerPasskeyRequestPublicKey.authenticatorSelection.authenticatorAttachment,
    )
    assertEquals(
      authenticatorSelectionRequireResidentKey,
      registerPasskeyRequestPublicKey.authenticatorSelection.requireResidentKey,
    )
    assertEquals(
      authenticatorSelectionResidentKey,
      registerPasskeyRequestPublicKey.authenticatorSelection.residentKey,
    )
    assertEquals(
      authenticatorSelectionUserVerification,
      registerPasskeyRequestPublicKey.authenticatorSelection.userVerification,
    )
    assertEquals(attestation, registerPasskeyRequestPublicKey.attestation)
  }

  @Test
  fun `Map to passkey fetch payload`() {
    val challenge = "WUMwNDk4ZWNlYzZhZWYwYWViZjRmNmJkZjBkMTZlOGUyNw"
    val timeout = 300000L
    val rpId = "example.com"
    val allowCredential =
      KeyCredential(
        id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ",
        type = "public-key",
        listOf("internal", "hybrid"),
      )
    val userVerification = "preferred"
    val payload =
      """
      {
        "publicKey": {
          "challenge": "$challenge",
          "timeout": $timeout,
          "rpId": "$rpId",
          "allowCredentials": [
              {
                  "id": "${allowCredential.id}",
                  "type": "${allowCredential.type}",
                  "transports": [
                      "${allowCredential.transports[0]}",
                      "${allowCredential.transports[1]}"
                  ]
              }
          ],
          "userVerification": "$userVerification"
        }
      }
      """.trimIndent()

    val authenticatePasskeyRequestPublicKey =
      passkeyPayloadMapper.mapToPasskeyAuthenticationPayload(payload).publicKey

    assertEquals(
      challenge.b64Decode().b64Encode(),
      authenticatePasskeyRequestPublicKey.challenge,
    )
    assertEquals(timeout, authenticatePasskeyRequestPublicKey.timeout)
    assertEquals(rpId, authenticatePasskeyRequestPublicKey.rpId)
    assertEquals(
      allowCredential.id,
      authenticatePasskeyRequestPublicKey.allowCredentials.first().id,
    )
    assertEquals(
      allowCredential.type,
      authenticatePasskeyRequestPublicKey.allowCredentials.first().type,
    )
    assertEquals(
      allowCredential.transports,
      authenticatePasskeyRequestPublicKey.allowCredentials.first().transports,
    )
    assertEquals(userVerification, authenticatePasskeyRequestPublicKey.userVerification)
  }

  @Test
  fun `Map to passkey creation response`() {
    val id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ"
    val rawId = "eb24a684977aa8650c0ad862aaccf26f839de14e93167d2fdc32f3fb5119acd4"
    val authenticatorAttachment = "platform"
    val attestationObject =
      "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViko3mm9u6vuaVeN4wRgDTidR5oL6ufLTCrE9ISVYbOGUdFAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAIOskpoSXeqhlDArYYqrM8m-DneFOkxZ9L9wy8_tRGazUpQECAyYgASFYIOP8op-6gZJ1H0wHNLe2k" +
        "7cgBqaSUR4pdcBqtOHPaYUhIlggsXZWuWfZS-6YpbeU9dsB2UoroACSKXJOrdE_auaUIf4"
    val clientDataJSON =
      "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiV1VZd05EaGtNV0UzWldNellUSmhOamsz" +
        "TURBMU9XTXlOelkyWW1Kak4yVXdaZyIsIm9yaWdpbiI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjcm9zc09yaWdpbiI6ZmFsc2V9"
    val type = "public-key"
    val transports = listOf("internal", "hybrid")
    val payload =
      """
      {
          "id": "$id",
          "rawId": "$rawId",
          "authenticatorAttachment": "$authenticatorAttachment",
          "response": {
              "attestationObject": "$attestationObject",
              "clientDataJSON": "$clientDataJSON",
              "transports": [
                  "${transports[0]}",
                  "${transports[1]}"
              ]
          },
          "type": "$type"
      }
      """.trimIndent()

    val registerPasskeyResponse =
      passkeyPayloadMapper.mapToPasskeyCreationResponse(payload)

    assertEquals(id, registerPasskeyResponse.id)
    assertEquals(rawId, registerPasskeyResponse.rawId)
    assertEquals(authenticatorAttachment, registerPasskeyResponse.authenticatorAttachment)
    assertEquals(attestationObject, registerPasskeyResponse.attestationObject)
    assertEquals(clientDataJSON, registerPasskeyResponse.clientDataJSON)
    assertEquals(transports, registerPasskeyResponse.transports)
    assertEquals(type, registerPasskeyResponse.type)
  }

  @Test
  fun `Map to authenticate passkey response`() {
    val id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ"
    val rawId = "eb24a684977aa8650c0ad862aaccf26f839de14e93167d2fdc32f3fb5119acd4"
    val authenticatorAttachment = "platform"
    val type = "public-key"
    val signature =
      "MEYCIQDDs662ykELzpmxkQaOR6HY5GwO7nX5z7jc7q9GbWZmvwIhAMEm4VBjWKzn60eGF8VtO6uqkRtSQpJvixCEy9Pr6E4o"
    val userHandle = "WUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0NjA3YjIxOTEyYg"
    val clientDataJSON =
      "eyJ0eXBlIjoid2ViYXV0aG4uZ2V0IiwiY2hhbGxlbmdlIjoiV1VNd05EazRaV05sWXpaaFpXWXdZV1ZpWmpSbU5tSmtaak" +
        "JrTVRabE9HVXlOdyIsIm9yaWdpbiI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjcm9zc09yaWdpbiI6ZmFsc2V9"
    val authenticatorData = "o3mm9u6vuaVeN4wRgDTidR5oL6ufLTCrE9ISVYbOGUcFAAAAAQ"
    val payload =
      """
      {
          "id": "$id",
          "rawId": "$rawId",
          "authenticatorAttachment": "$authenticatorAttachment",
          "type": "$type",
          "response": {
              "signature": "$signature",
              "userHandle": "$userHandle",
              "clientDataJSON": "$clientDataJSON",
              "authenticatorData": "$authenticatorData"
          }
      }
      """.trimIndent()

    val authenticatePasskeyResponse =
      passkeyPayloadMapper.mapToAuthenticatePasskeyResponse(payload)

    assertEquals(id, authenticatePasskeyResponse.id)
    assertEquals(rawId, authenticatePasskeyResponse.rawId)
    assertEquals(authenticatorAttachment, authenticatePasskeyResponse.authenticatorAttachment)
    assertEquals(type, authenticatePasskeyResponse.type)
    assertEquals(signature, authenticatePasskeyResponse.signature)
    assertEquals(userHandle, authenticatePasskeyResponse.userHandle)
    assertEquals(clientDataJSON, authenticatePasskeyResponse.clientDataJSON)
    assertEquals(authenticatorData, authenticatePasskeyResponse.authenticatorData)
  }

  @Test
  fun `Map SerializationException to TwilioException`() {
    val serializationException = SerializationException("Error serializing")
    val twilioException = passkeyPayloadMapper.mapException(serializationException)
    assertEquals(twilioException.message, "$INVALID_JSON_PAYLOAD_ERROR: ${serializationException.message}")
  }

  @Test
  fun `Map IllegalArgumentException to TwilioException`() {
    val illegalArgumentException = IllegalArgumentException("Illegal argument")
    val twilioException = passkeyPayloadMapper.mapException(illegalArgumentException)
    assertEquals(twilioException.message, "$INVALID_JSON_PAYLOAD_ERROR: ${illegalArgumentException.message}")
  }

  @Test
  fun `Map IndexOutOfBoundsException to TwilioException`() {
    val indexOutOfBoundsException = IndexOutOfBoundsException("Index out of bounds")
    val twilioException = passkeyPayloadMapper.mapException(indexOutOfBoundsException)
    assertEquals(twilioException.message, "$INVALID_JSON_PAYLOAD_ERROR: ${indexOutOfBoundsException.message}")
  }

  @Test
  fun `Map Exception to TwilioException`() {
    val unknownsException = Exception("unknown")
    val twilioException = passkeyPayloadMapper.mapException(unknownsException)
    assertEquals(twilioException.message, "$UNKNOWN_ERROR: ${unknownsException.message}")
  }
}
