package com.twilio.passkeys.android.repository

import com.twilio.passkeys.android.api.AuthenticateApi
import com.twilio.passkeys.android.api.AuthenticateVerificationRequest
import com.twilio.passkeys.android.model.AuthenticateStartResponse
import com.twilio.passkeys.android.model.AuthenticateVerificationResponse
import javax.inject.Inject

class AuthenticateRepository
  @Inject
  constructor(private val authenticateApi: AuthenticateApi) {
    suspend fun start(): AuthenticateStartResponse {
      return authenticateApi.authenticateStart()
    }

    suspend fun verification(
      rawId: String,
      id: String,
      clientDataJson: String,
      userHandle: String?,
      signature: String?,
      authenticatorData: String?,
    ): AuthenticateVerificationResponse {
      return authenticateApi.authenticateVerification(
        AuthenticateVerificationRequest(
          rawId = rawId,
          id = id,
          clientDataJson = clientDataJson,
          userHandle = userHandle,
          signature = signature,
          authenticatorData = authenticatorData,
        ),
      )
    }
  }
