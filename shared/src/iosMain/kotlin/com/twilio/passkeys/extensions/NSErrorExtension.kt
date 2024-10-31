package com.twilio.passkeys.extensions

import com.twilio.passkeys.exception.TwilioException
import platform.Foundation.NSError

internal const val PASSKEY_CANCELED_ERROR_CODE = 1001L
internal const val PASSKEY_INVALID_RESPONSE_ERROR_CODE = 1002L
internal const val PASSKEY_NOT_HANDLED_ERROR_CODE = 1003L
internal const val PASSKEY_FAILED_ERROR_CODE = 1004L
internal const val PASSKEY_NOT_INTERACTIVE_ERROR_CODE = 1005L
internal const val PASSKEY_UNSUPPORTED_ERROR_CODE = 1006L
internal const val DOM_MESSAGE =
  """
    DOM errors thrown according to the WebAuthn spec.
    Ensure `/.well-known/apple-app-site-association` is correctly set up
    as described here: https://developer.apple.com/documentation/xcode/supporting-associated-domains
  """

internal fun NSError.toTwilioException(): TwilioException {
  return when (this.code) {
    PASSKEY_CANCELED_ERROR_CODE -> TwilioException.UserCanceledException(Throwable(this.localizedDescription))
    PASSKEY_INVALID_RESPONSE_ERROR_CODE -> TwilioException.DomException(DOM_MESSAGE, Throwable(this.localizedDescription))
    PASSKEY_UNSUPPORTED_ERROR_CODE -> TwilioException.UnsupportedException(Throwable(this.localizedDescription))
    PASSKEY_NOT_HANDLED_ERROR_CODE,
    PASSKEY_NOT_INTERACTIVE_ERROR_CODE,
    PASSKEY_FAILED_ERROR_CODE,
    -> TwilioException.GeneralException(Throwable(this.localizedDescription))

    else -> TwilioException.GeneralException(Throwable(this.localizedDescription))
  }
}
