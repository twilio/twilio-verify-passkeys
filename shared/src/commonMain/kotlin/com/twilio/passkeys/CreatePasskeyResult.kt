package com.twilio.passkeys

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.models.CreatePasskeyResponse

sealed class CreatePasskeyResult {
  data class Success(val createPasskeyResponse: CreatePasskeyResponse) : CreatePasskeyResult()

  data class Error(val error: TwilioException) : CreatePasskeyResult()
}
