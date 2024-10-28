/*
 * Copyright © 2024 Twilio Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twilio.passkeys.exception

internal const val DOM_EXCEPTION = 1001
internal const val USER_CANCELED_EXCEPTION = 1002
internal const val INTERRUPTED_EXCEPTION = 1003
internal const val UNSUPPORTED_EXCEPTION = 1004
internal const val NO_CREDENTIAL_EXCEPTION = 1005
internal const val MISSING_ATTESTATION_OBJECT_EXCEPTION = 1006
internal const val INVALID_JSON_PAYLOAD_EXCEPTION = 1008
internal const val GENERAL_EXCEPTION = 1009

/**
 * Represents an exception specific to Twilio.
 * This exception is used to encapsulate error messages with their types.
 *
 * @property code The error code.
 * @property message The error message.
 * @property cause The error cause.
 */
sealed class TwilioException2(val code: Int, override val message: String, override val cause: Throwable) : Exception(message, cause) {
  /**
   * Handles DOM-related passkey errors as specified by the WebAuthn standard.
   * Errors may occur if the following configurations are not correctly set:
   * - On Android: Ensure `/.well-known/assetlinks.json` is properly configured as outlined here: https://developer.android.com/identity/sign-in/credential-manager#add-support-dal
   * - On iOS: Ensure `/.well-known/apple-app-site-association` is correctly set up as described here: https://developer.apple.com/documentation/xcode/supporting-associated-domains
   */
  data class DomException(override val message: String, override val cause: Throwable) : TwilioException2(DOM_EXCEPTION, message, cause)

  /**
   * Represents an exception for user-initiated cancellations.
   * Thrown when the user intentionally cancels an operation, allowing
   * for graceful handling of voluntary action termination.
   */
  data class UserCanceledException(override val cause: Throwable) :
    TwilioException2(USER_CANCELED_EXCEPTION, "User intentionally canceled the operation", cause)

  /**
   * Exception indicating an operation was interrupted and is potentially recoverable.
   * Suggests that the operation may be retried to complete successfully.
   */
  data class InterruptedException(override val cause: Throwable) :
    TwilioException2(INTERRUPTED_EXCEPTION, "Retry-able error. Consider retrying", cause)

  /**
   * Exception indicating that the device either does not support passkeys
   * or has the passkeys feature disabled.
   * Thrown when passkey-related operations cannot be performed due to device limitations.
   */
  data class UnsupportedException(override val cause: Throwable) :
    TwilioException2(UNSUPPORTED_EXCEPTION, "Device either has disabled passkeys feature or doesn't support it", cause)

  /**
   * Exception thrown when there are no passkey credentials available for the user.
   * This indicates that the user has not set up any credentials for authentication.
   */
  data class NoCredentialException(override val cause: Throwable) :
    TwilioException2(NO_CREDENTIAL_EXCEPTION, "No passkey credential is available for the user", cause)

  /**
   * [iOS only]
   * Exception thrown when the attestation object is null.
   * This indicates that the expected attestation object is missing and must be provided
   * for successful passkey operations.
   */
  data class MissingAttestationObjectException(override val cause: Throwable) :
    TwilioException2(MISSING_ATTESTATION_OBJECT_EXCEPTION, "Attestation object should not be null", cause)

  /**
   * Exception indicating an invalid JSON payload.
   * Thrown when the JSON data does not conform to the expected format or schema.
   */
  data class InvalidPayloadException(override val cause: Throwable) :
    TwilioException2(INVALID_JSON_PAYLOAD_EXCEPTION, "JSON payload is not valid", cause)

  /**
   * Represents a general or unspecified exception.
   * Used as a fallback for errors that do not fit specific categories.
   */
  data class GeneralException(override val cause: Throwable) : TwilioException2(GENERAL_EXCEPTION, "An unexpected error occurred", cause)
}
