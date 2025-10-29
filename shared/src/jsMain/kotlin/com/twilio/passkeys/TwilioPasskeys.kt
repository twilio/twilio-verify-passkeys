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

import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.Promise

@JsModule("./CredentialManager")
@JsNonModule
@JsName("CredentialManager")
external class CredentialManager {
  companion object {
    fun createCredential(createPasskeyRequest: String): Promise<String>
    fun getCredential(authenticatePasskeyRequest: String): Promise<String>
  }
}


actual class TwilioPasskeys internal constructor(
  private val passkeysPayloadMapper: PasskeyPayloadMapper
) {

  constructor() : this(
    passkeysPayloadMapper = PasskeyPayloadMapper
  )

  actual suspend fun create(
    createPasskeyRequest: CreatePasskeyRequest,
    appContext: AppContext
  ): CreatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun create(createPayload: String, appContext: AppContext): CreatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
    appContext: AppContext
  ): AuthenticatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun authenticate(
    authenticatePayload: String,
    appContext: AppContext
  ): AuthenticatePasskeyResult {
    TODO("Not yet implemented")
  }
}


@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("TwilioPasskeys")
class TwilioPasskeysJS private constructor(
  private val credentialManager: CredentialManager.Companion,
  private val passkeysPayloadMapper: PasskeyPayloadMapper
) {

  @JsName("withMapper")
  constructor() : this(
    CredentialManager,
    PasskeyPayloadMapper
  )


  private fun create(createPasskeyRequest: CreatePasskeyRequest): Promise<CreatePasskeyResult> {
    val requestJson = Json.encodeToString(createPasskeyRequest)
    return CredentialManager.createCredential(requestJson).then {
      CreatePasskeyResult.Success(passkeysPayloadMapper.mapToCreatePasskeyResponse(it))
    }
  }


  fun create(
    challengePayload: String,
    appContext: AppContext
  ): Promise<CreatePasskeyResult> {
    val createPasskeyRequest = passkeysPayloadMapper.mapToCreatePasskeyResponse(challengePayload).toString()
    return create(createPasskeyRequest,appContext)
  }

  private fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
  ): Promise<AuthenticatePasskeyResult> {
    val requestJson = Json.encodeToString(authenticatePasskeyRequest.publicKey)
    return CredentialManager.getCredential(requestJson).then {
      AuthenticatePasskeyResult.Success(passkeysPayloadMapper.mapToAuthenticatePasskeyResponse(it))
    }
  }

  fun authenticate(challengePayload: String, appContext: AppContext): Promise<AuthenticatePasskeyResult> {
    val authenticatePasskeyRequest =
      passkeysPayloadMapper.mapToAuthenticatePasskeyResponse(challengePayload).toString()
    return authenticate(authenticatePasskeyRequest, appContext)
  }
}

actual class AppContext()
