package com.twilio.passkeys.extensions

import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.CreateCredentialUnsupportedException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialException
import com.twilio.passkeys.exception.TwilioException

internal const val DOM_MESSAGE =
  """
    DOM errors thrown according to the WebAuthn spec.
    Ensure `/.well-known/assetlinks.json` is properly configured
    as outlined here: https://developer.android.com/identity/sign-in/credential-manager#add-support-dal
  """

internal fun GetCredentialException.toTwilioException(): TwilioException {
  return when (this) {
    is GetCredentialCancellationException -> TwilioException.UserCanceledException(this)
    is GetCredentialInterruptedException -> TwilioException.InterruptedException(this)
    is GetCredentialUnsupportedException -> TwilioException.UnsupportedException(this)
    is GetPublicKeyCredentialDomException -> TwilioException.DomException(DOM_MESSAGE.trimIndent(), this)
    is NoCredentialException -> TwilioException.NoCredentialException(this)
    is GetPublicKeyCredentialException,
    is GetCredentialUnknownException,
    is GetCredentialCustomException,
    is GetCredentialProviderConfigurationException,
    -> TwilioException.GeneralException(this)

    else -> TwilioException.GeneralException(this)
  }
}

internal fun CreateCredentialException.toTwilioException(): TwilioException {
  return when (this) {
    is CreateCredentialCancellationException -> TwilioException.UserCanceledException(this)
    is CreateCredentialInterruptedException -> TwilioException.InterruptedException(this)
    is CreateCredentialUnsupportedException -> TwilioException.UnsupportedException(this)
    is CreatePublicKeyCredentialDomException -> TwilioException.DomException(DOM_MESSAGE.trimIndent(), this)
    is CreateCredentialNoCreateOptionException,
    is CreatePublicKeyCredentialException,
    is CreateCredentialUnknownException,
    is CreateCredentialCustomException,
    is CreateCredentialProviderConfigurationException,
    -> TwilioException.GeneralException(this)

    else -> TwilioException.GeneralException(this)
  }
}
