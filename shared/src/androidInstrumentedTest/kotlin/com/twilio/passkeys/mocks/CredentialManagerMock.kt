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

package com.twilio.passkeys.mocks

import android.app.PendingIntent
import android.content.Context
import android.os.CancellationSignal
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreateCredentialRequest
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PrepareGetCredentialResponse
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.twilio.passkeys.models.AuthenticatePasskeyDto
import com.twilio.passkeys.models.AuthenticatePasskeyResponseDto
import com.twilio.passkeys.models.CreatePasskeyDto
import com.twilio.passkeys.models.CreatePasskeyResponseDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executor

val createPasskeyChallengePayload =
  """
  {
    "rp": {
      "id": "example.com",
      "name": "Example"
    },
    "user": {
      "id": "WUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0NjA3YjIxOTEyYg",
      "name": "user1",
      "displayName": "User One"
    },
    "challenge": "WUYwNDhkMWE3ZWMzYTJhNjk3MDA1OWMyNzY2YmJjN2UwZg",
    "pubKeyCredParams": [
      {
        "type": "public-key",
        "alg": -7
      }
    ],
    "timeout": 600000,
    "excludeCredentials": [

    ],
    "authenticatorSelection": {
      "authenticatorAttachment": "platform",
      "requireResidentKey": false,
      "residentKey": "preferred",
      "userVerification": "preferred"
    },
    "attestation": "none"
  }
  """.trimIndent()
val creationResponse =
  CreatePasskeyDto(
    id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ",
    rawId = "eb24a684977aa8650c0ad862aaccf26f839de14e93167d2fdc32f3fb5119acd4",
    authenticatorAttachment = "platform",
    type = "public-key",
    response =
      CreatePasskeyResponseDto(
        attestationObject =
          "o2NmbXRkbm9uZWdhdHRTdG10oGhhdXRoRGF0YViko3mm9u6vuaVeN4wRgDTidR5" +
            "oL6ufLTCrE9ISVYbOGUdFAAAAAAAAAAAAAAAAAAAAAAAAAAAAIOskpoSXeqhlDArYY" +
            "qrM8m-DneFOkxZ9L9wy8_tRGazUpQECAyYgASFYIOP8op-6gZJ1H0wHNLe2k7cgBqaSUR4pdc" +
            "BqtOHPaYUhIlggsXZWuWfZS-6YpbeU9dsB2UoroACSKXJOrdE_auaUIf4",
        clientDataJSON =
          "eyJ0eXBlIjoid2ViYXV0aG4uY3JlYXRlIiwiY2hhbGxlbmdlIjoiV1VZd05EaGtNV0UzWldNellUSmhOamsz" +
            "TURBMU9XTXlOelkyWW1Kak4yVXdaZyIsIm9yaWdpbiI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjcm9zc09yaWdpbiI6ZmFsc2V9",
        transports = listOf("internal", "hybrid"),
      ),
  )
var createCredentialException: CreateCredentialException? = null

val authenticatePasskeyChallengePayload =
  """
  {
    "publicKey": {
      "challenge": "WUMwNDk4ZWNlYzZhZWYwYWViZjRmNmJkZjBkMTZlOGUyNw",
      "timeout": 300000,
      "rpId": "example.com",
      "allowCredentials": [
        {
          "id": "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ",
          "type": "public-key",
          "transports": [
            "internal"
          ]
        }
      ],
      "userVerification": "preferred",
      "extensions": {

      }
    }
  }
  """.trimIndent()
val authenticationResponse =
  AuthenticatePasskeyDto(
    rawId = "eb24a684977aa8650c0ad862aaccf26f839de14e93167d2fdc32f3fb5119acd4",
    id = "6ySmhJd6qGUMCthiqszyb4Od4U6TFn0v3DLz-1EZrNQ",
    authenticatorAttachment = "platform",
    type = "public-key",
    response =
      AuthenticatePasskeyResponseDto(
        clientDataJSON =
          "eyJ0eXBlIjoid2ViYXV0aG4uZ2V0IiwiY2hhbGxlbmdlIjoiV1VNd05EazRaV05sWXpaaFpXWXdZV1Zp" +
            "WmpSbU5tSmtaakJrTVRabE9HVXlOdyIsIm9yaWdpbiI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjcm9zc09yaWdpbiI6ZmFsc2V9",
        authenticatorData = "o3mm9u6vuaVeN4wRgDTidR5oL6ufLTCrE9ISVYbOGUcFAAAAAQ",
        signature = "MEYCIQDDs662ykELzpmxkQaOR6HY5GwO7nX5z7jc7q9GbWZmvwIhAMEm4VBjWKzn60eGF8VtO6uqkRtSQpJvixCEy9Pr6E4o",
        userHandle = "WUU0ZmQzYWFmNGU0NTMyNGQwZjNlMTM0NjA3YjIxOTEyYg",
      ),
  )
var authenticateCredentialException: GetCredentialException? = null

class CredentialManagerMock : CredentialManager {
  override fun clearCredentialStateAsync(
    request: ClearCredentialStateRequest,
    cancellationSignal: CancellationSignal?,
    executor: Executor,
    callback: CredentialManagerCallback<Void?, ClearCredentialException>,
  ) {
    TODO("Not yet implemented")
  }

  override fun createCredentialAsync(
    context: Context,
    request: CreateCredentialRequest,
    cancellationSignal: CancellationSignal?,
    executor: Executor,
    callback: CredentialManagerCallback<CreateCredentialResponse, CreateCredentialException>,
  ) {
    if (createCredentialException != null) {
      callback.onError(createCredentialException!!)
    } else {
      callback.onResult(getCreatePublicKeyCredentialResponse())
    }
  }

  private fun getCreatePublicKeyCredentialResponse(): CreatePublicKeyCredentialResponse {
    val registrationResponseJson = Json.encodeToString(creationResponse)
    return CreatePublicKeyCredentialResponse(registrationResponseJson)
  }

  override fun createSettingsPendingIntent(): PendingIntent {
    TODO("Not yet implemented")
  }

  override fun getCredentialAsync(
    context: Context,
    request: GetCredentialRequest,
    cancellationSignal: CancellationSignal?,
    executor: Executor,
    callback: CredentialManagerCallback<GetCredentialResponse, GetCredentialException>,
  ) {
    if (authenticateCredentialException != null) {
      callback.onError(authenticateCredentialException!!)
    } else {
      callback.onResult(getGetCredentialResponse())
    }
  }

  private fun getGetCredentialResponse(): GetCredentialResponse {
    val authenticationResponseJson = Json.encodeToString(authenticationResponse)
    return GetCredentialResponse(PublicKeyCredential(authenticationResponseJson))
  }

  override fun getCredentialAsync(
    context: Context,
    pendingGetCredentialHandle: PrepareGetCredentialResponse.PendingGetCredentialHandle,
    cancellationSignal: CancellationSignal?,
    executor: Executor,
    callback: CredentialManagerCallback<GetCredentialResponse, GetCredentialException>,
  ) {
    TODO("Not yet implemented")
  }

  override fun prepareGetCredentialAsync(
    request: GetCredentialRequest,
    cancellationSignal: CancellationSignal?,
    executor: Executor,
    callback: CredentialManagerCallback<PrepareGetCredentialResponse, GetCredentialException>,
  ) {
    TODO("Not yet implemented")
  }
}
