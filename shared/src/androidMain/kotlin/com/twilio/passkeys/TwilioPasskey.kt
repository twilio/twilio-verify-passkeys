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
import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest
import com.twilio.passkeys.AppContext
import com.twilio.passkeys.AuthenticatePasskeyResult
import com.twilio.passkeys.CreatePasskeyResult
import com.twilio.passkeys.PasskeyPayloadMapper
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual class TwilioPasskey internal constructor(
  private val credentialManager: CredentialManager,
  private val passkeyPayloadMapper: PasskeyPayloadMapper,
) {
  constructor(context: Context) : this(
    CredentialManager.create(context),
    PasskeyPayloadMapper,
  )

  actual suspend fun create(
    createPasskeyRequest: CreatePasskeyRequest,
    appContext: AppContext,
  ): CreatePasskeyResult {
    try {
      val requestJson = Json.encodeToString(createPasskeyRequest)
println("felo requestJson: $requestJson")
      val credentialManagerResult =
        credentialManager.createCredential(
          context = appContext.activity,
          request = CreatePublicKeyCredentialRequest(requestJson = requestJson),
        )
println("felo 2")
      val registrationResponseJson =
        (credentialManagerResult as CreatePublicKeyCredentialResponse).registrationResponseJson

      return CreatePasskeyResult.Success(
        passkeyPayloadMapper.mapToPasskeyCreationResponse(
          registrationResponseJson,
        ),
      )
    } catch (e: CreateCredentialException) {
      println("felo 3: "+e.errorMessage.toString())
      val error = TwilioException(e.type, e.errorMessage.toString())
      return CreatePasskeyResult.Error(error)
    }
  }

  actual suspend fun create(
    challengePayload: String,
    appContext: AppContext,
  ): CreatePasskeyResult {
    return try {
      val createPasskeyRequest =
        passkeyPayloadMapper.mapToPasskeyCreationPayload(challengePayload)
      create(createPasskeyRequest, appContext)
    } catch (e: Exception) {
      CreatePasskeyResult.Error(passkeyPayloadMapper.mapException(e))
    }
  }

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
      val error = TwilioException(e.type, e.errorMessage.toString())
      return AuthenticatePasskeyResult.Error(error)
    }
  }

  actual suspend fun authenticate(
    challengePayload: String,
    appContext: AppContext,
  ): AuthenticatePasskeyResult {
    return try {
      val authenticatePasskeyRequest =
        passkeyPayloadMapper.mapToPasskeyAuthenticationPayload(challengePayload)
      authenticate(authenticatePasskeyRequest, appContext)
    } catch (e: Exception) {
      AuthenticatePasskeyResult.Error(passkeyPayloadMapper.mapException(e))
    }
  }
}

actual class AppContext(val activity: Activity)
