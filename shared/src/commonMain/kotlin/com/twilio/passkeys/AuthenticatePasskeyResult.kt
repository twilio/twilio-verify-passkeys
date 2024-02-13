package com.twilio.passkeys

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.models.AuthenticatePasskeyResponse

sealed class AuthenticatePasskeyResult {
  data class Success(val authenticatePasskeyResponse: AuthenticatePasskeyResponse) :
    AuthenticatePasskeyResult()

  data class Error(val error: TwilioException) : AuthenticatePasskeyResult()
}
