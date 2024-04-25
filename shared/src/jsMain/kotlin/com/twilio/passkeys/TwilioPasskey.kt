
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


actual open class TwilioPasskey private constructor(
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

  actual suspend fun create(challengePayload: String, appContext: AppContext): CreatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
    appContext: AppContext
  ): AuthenticatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun authenticate(
    challengePayload: String,
    appContext: AppContext
  ): AuthenticatePasskeyResult {
    TODO("Not yet implemented")
  }
}


@OptIn(ExperimentalJsExport::class)
@JsExport
@JsName("TwilioPasskey")
class TwilioPasskeyJS private constructor(
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
      CreatePasskeyResult.Success(passkeysPayloadMapper.mapToPasskeyCreationResponse(it))
    }
  }


  fun create(
    challengePayload: String,
  ): Promise<CreatePasskeyResult> {
    val createPasskeyRequest = passkeysPayloadMapper.mapToPasskeyCreationPayload(challengePayload)
    return create(createPasskeyRequest)
  }

  private fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
  ): Promise<AuthenticatePasskeyResult> {
    val requestJson = Json.encodeToString(authenticatePasskeyRequest.publicKey)
    return CredentialManager.getCredential(requestJson).then {
      AuthenticatePasskeyResult.Success(passkeysPayloadMapper.mapToAuthenticatePasskeyResponse(it))
    }
  }

  fun authenticate(challengePayload: String): Promise<AuthenticatePasskeyResult> {
    val authenticatePasskeyRequest =
      passkeysPayloadMapper.mapToPasskeyAuthenticationPayload(challengePayload)
    return authenticate(authenticatePasskeyRequest)
  }
}

actual class AppContext()
