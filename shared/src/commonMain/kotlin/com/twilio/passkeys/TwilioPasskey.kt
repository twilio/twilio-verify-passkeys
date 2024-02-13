package com.twilio.passkeys

import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest

expect class TwilioPasskey {
  suspend fun create(
      createPasskeyRequest: CreatePasskeyRequest,
      appContext: AppContext,
  ): CreatePasskeyResult

  suspend fun create(
      challengePayload: String,
      appContext: AppContext,
  ): CreatePasskeyResult

  suspend fun authenticate(
      authenticatePasskeyRequest: AuthenticatePasskeyRequest,
      appContext: AppContext,
  ): AuthenticatePasskeyResult

  suspend fun authenticate(
      challengePayload: String,
      appContext: AppContext,
  ): AuthenticatePasskeyResult
}

expect class AppContext
