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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.twilio.passkeys

import android.app.Activity
import android.content.Context
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.twilio.passkeys.extensions.toTwilioException
import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Represents the Twilio Passkey class responsible for managing passkey operations.
 *
 * @property credentialManager The credential manager responsible for managing passkey credentials.
 * @property passkeyPayloadMapper The passkey payload mapper used for mapping passkey payloads and responses.
 */
actual open class TwilioPasskeys internal constructor(
  private val credentialManager: CredentialManager,
  private val passkeyPayloadMapper: PasskeyPayloadMapper,
) {
  /**
   * Constructor for creating a TwilioPasskey instance with the provided context.
   *
   * @param context The Android activity context.
   */
  constructor(context: Context) : this(
    CredentialManager.create(context),
    PasskeyPayloadMapper,
  )

  /**
   * Creates a passkey using the provided [createPasskeyRequest] and [appContext].
   *
   * @param createPasskeyRequest The request for creating the passkey.
   * @param appContext The activity context.
   * @return The result of creating the passkey.
   */
  actual suspend fun create(
    createPasskeyRequest: CreatePasskeyRequest,
    appContext: AppContext,
  ): CreatePasskeyResult {
    try {
      val requestJson = Json.encodeToString(createPasskeyRequest)
      val credentialManagerResult =
        credentialManager.createCredential(
          context = appContext.activity,
          request = CreatePublicKeyCredentialRequest(requestJson = requestJson),
        )
      val registrationResponseJson =
        (credentialManagerResult as CreatePublicKeyCredentialResponse).registrationResponseJson

      return CreatePasskeyResult.Success(
        passkeyPayloadMapper.mapToCreatePasskeyResponse(
          registrationResponseJson,
        ),
      )
    } catch (e: CreateCredentialException) {
      return CreatePasskeyResult.Error(e.toTwilioException())
    }
  }

  /**
   * Creates a passkey using the provided [createPayload] and [appContext].
   *
   * @param createPayload The payload for creating the passkey.
   * @param appContext The activity context.
   * @return The result of creating the passkey.
   */
  actual suspend fun create(
    createPayload: String,
    appContext: AppContext,
  ): CreatePasskeyResult {
    return try {
      val createPasskeyRequest =
        passkeyPayloadMapper.mapToCreatePasskeyRequest(createPayload)
      create(createPasskeyRequest, appContext)
    } catch (e: Exception) {
      CreatePasskeyResult.Error(passkeyPayloadMapper.mapException(e))
    }
  }

  /**
   * Authenticates a passkey using the provided [authenticatePasskeyRequest] and [appContext].
   *
   * @param authenticatePasskeyRequest The request for authenticating the passkey.
   * @param appContext The activity context.
   * @return The result of authenticating the passkey.
   */
  actual suspend fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
    appContext: AppContext,
  ): AuthenticatePasskeyResult {
    try {
      val requestJson = Json.encodeToString(authenticatePasskeyRequest.publicKey)
      val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(requestJson)
      val getCredRequest = GetCredentialRequest(listOf(getPublicKeyCredentialOption))
      val response = credentialManager.getCredential(appContext.activity, getCredRequest)

      val authenticationResponseJson =
        (response.credential as PublicKeyCredential).authenticationResponseJson

      return AuthenticatePasskeyResult.Success(
        passkeyPayloadMapper.mapToAuthenticatePasskeyResponse(
          authenticationResponseJson,
        ),
      )
    } catch (e: GetCredentialException) {
      return AuthenticatePasskeyResult.Error(e.toTwilioException())
    }
  }

  /**
   * Authenticates a passkey using the provided [authenticatePayload] and [appContext].
   *
   * @param authenticatePayload The payload for authenticating the passkey.
   * @param appContext The activity context.
   * @return The result of authenticating the passkey.
   */
  actual suspend fun authenticate(
    authenticatePayload: String,
    appContext: AppContext,
  ): AuthenticatePasskeyResult {
    return try {
      val authenticatePasskeyRequest =
        passkeyPayloadMapper.mapToAuthenticatePasskeyRequest(authenticatePayload)
      authenticate(authenticatePasskeyRequest, appContext)
    } catch (e: Exception) {
      AuthenticatePasskeyResult.Error(passkeyPayloadMapper.mapException(e))
    }
  }
}

/**
 * Represents the activity context.
 *
 * @property activity The Android activity.
 */
actual open class AppContext(open val activity: Activity)
