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
import com.twilio.passkeys.exception.TwilioException2

internal const val DOM_MESSAGE =
  """
    DOM errors thrown according to the WebAuthn spec.
    Ensure `/.well-known/assetlinks.json` is properly configured
    as outlined here: https://developer.android.com/identity/sign-in/credential-manager#add-support-dal
  """

internal fun GetCredentialException.toTwilioException(): TwilioException2 {
  return when (this) {
    is GetCredentialCancellationException -> TwilioException2.UserCanceledException(this)
    is GetCredentialInterruptedException -> TwilioException2.InterruptedException(this)
    is GetCredentialUnsupportedException -> TwilioException2.UnsupportedException(this)
    is GetPublicKeyCredentialDomException -> TwilioException2.DomException(DOM_MESSAGE.trimIndent(), this)
    is NoCredentialException -> TwilioException2.NoCredentialException(this)
    is GetPublicKeyCredentialException,
    is GetCredentialUnknownException,
    is GetCredentialCustomException,
    is GetCredentialProviderConfigurationException,
    -> TwilioException2.GeneralException(this)

    else -> TwilioException2.GeneralException(this)
  }
}

internal fun CreateCredentialException.toTwilioException(): TwilioException2 {
  return when (this) {
    is CreateCredentialCancellationException -> TwilioException2.UserCanceledException(this)
    is CreateCredentialInterruptedException -> TwilioException2.InterruptedException(this)
    is CreateCredentialUnsupportedException -> TwilioException2.UnsupportedException(this)
    is CreatePublicKeyCredentialDomException -> TwilioException2.DomException(DOM_MESSAGE.trimIndent(), this)
    is CreateCredentialNoCreateOptionException,
    is CreatePublicKeyCredentialException,
    is CreateCredentialUnknownException,
    is CreateCredentialCustomException,
    is CreateCredentialProviderConfigurationException,
    -> TwilioException2.GeneralException(this)

    else -> TwilioException2.GeneralException(this)
  }
}
