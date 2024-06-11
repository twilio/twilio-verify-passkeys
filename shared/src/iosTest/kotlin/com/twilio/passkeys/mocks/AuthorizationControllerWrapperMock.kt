package com.twilio.passkeys.mocks

import com.twilio.passkeys.AuthenticatePasskeyResult
import com.twilio.passkeys.CreatePasskeyResult
import com.twilio.passkeys.FAILED_ERROR
import com.twilio.passkeys.IAuthorizationControllerWrapper
import com.twilio.passkeys.exception.TwilioException
import platform.AuthenticationServices.ASAuthorizationController

class AuthorizationControllerWrapperMock : IAuthorizationControllerWrapper {
  var createPasskeyResultValue: CreatePasskeyResult? = null
  var authenticatePasskeyResultValue: AuthenticatePasskeyResult? = null

  override fun createPasskey(
    authController: ASAuthorizationController,
    completion: (CreatePasskeyResult) -> Unit,
  ) {
    createPasskeyResultValue?.let {
      completion.invoke(it)
      return
    }

    completion(CreatePasskeyResult.Error(TwilioException("Unable to find a createPasskeyResultValue", FAILED_ERROR)))
  }

  override fun authenticatePasskey(
    authController: ASAuthorizationController,
    completion: (AuthenticatePasskeyResult) -> Unit,
  ) {
    authenticatePasskeyResultValue?.let {
      completion.invoke(it)
      return
    }

    completion(AuthenticatePasskeyResult.Error(TwilioException("Unable to find a authenticatePasskeyResultValue", FAILED_ERROR)))
  }
}
