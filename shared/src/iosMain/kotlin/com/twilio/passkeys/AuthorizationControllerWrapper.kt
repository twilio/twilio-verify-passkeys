package com.twilio.passkeys

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.exception.UNKNOWN_ERROR
import com.twilio.passkeys.models.AuthenticatePasskeyResponse
import com.twilio.passkeys.models.CreatePasskeyResponse
import com.twilio.passkeys.utils.DeviceUtils
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialAssertion
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialRegistration
import platform.AuthenticationServices.ASAuthorizationPublicKeyCredentialAttachment
import platform.Foundation.NSError
import platform.darwin.NSObject

interface IAuthorizationControllerWrapper {
  fun createPasskey(
    authController: ASAuthorizationController,
    completion: (CreatePasskeyResult) -> Unit,
  )

  fun authenticatePasskey(
    authController: ASAuthorizationController,
    completion: (AuthenticatePasskeyResult) -> Unit,
  )
}

/**
 * Wraps the ASAuthorizationController functionality in order to improve testability & maintainability.
 *
 * @property deviceUtils The utility class for device-related operations.
 */
class AuthorizationControllerWrapper : IAuthorizationControllerWrapper {
  private var authController: ASAuthorizationController? = null
  private var createPasskeyCompletion: ((CreatePasskeyResult) -> Unit)? = null
  private var authenticatePasskeyCompletion: ((AuthenticatePasskeyResult) -> Unit)? = null
  private val deviceUtils: DeviceUtils = DeviceUtils()

  override fun createPasskey(
    authController: ASAuthorizationController,
    completion: (CreatePasskeyResult) -> Unit,
  ) {
    this.createPasskeyCompletion = completion
    this.authController = authController
    this.authController?.delegate = createPasskeyDelegate
    this.authController?.performRequests()
  }

  override fun authenticatePasskey(
    authController: ASAuthorizationController,
    completion: (AuthenticatePasskeyResult) -> Unit,
  ) {
    this.authenticatePasskeyCompletion = completion
    this.authController = authController
    this.authController?.delegate = authenticatePasskeyDelegate
    this.authController?.performRequests()
  }

  private val createPasskeyDelegate =
    object : NSObject(), ASAuthorizationControllerDelegateProtocol {
      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization,
      ) {
        val credentialRegistration = didCompleteWithAuthorization.credential as ASAuthorizationPlatformPublicKeyCredentialRegistration

        val createPasskeyResponse: CreatePasskeyResponse =
          credentialRegistration.rawAttestationObject?.toUrlSafeString()
            ?.let { attestationObject ->
              CreatePasskeyResponse(
                id = credentialRegistration.credentialID.toUrlSafeString(),
                rawId = credentialRegistration.credentialID.toUrlSafeString(),
                authenticatorAttachment =
                  if (deviceUtils.isOSVersionSupported(ATTACHMENT_SUPPORT_MIN_OS_VERSION)) {
                    getAuthenticatorAttachment(credentialRegistration.attachment)
                  } else {
                    getAuthenticatorAttachment(ASAuthorizationPublicKeyCredentialAttachment.ASAuthorizationPublicKeyCredentialAttachmentPlatform)
                  },
                type = PASSKEY_TYPE,
                attestationObject = attestationObject,
                clientDataJSON = credentialRegistration.rawClientDataJSON.toUrlSafeString(),
                transports = listOf("internal"),
              )
            } ?: kotlin.run {
            createPasskeyCompletion?.invoke(
              CreatePasskeyResult.Error(
                TwilioException("Null attestation object", MISSING_ATTESTATION_OBJECT_ERROR),
              ),
            )
            return
          }

        createPasskeyCompletion?.invoke(
          CreatePasskeyResult.Success(
            createPasskeyResponse,
          ),
        )
      }

      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError,
      ) {
        val exception = mapToTwilioException(didCompleteWithError)
        createPasskeyCompletion?.invoke(CreatePasskeyResult.Error(exception))
      }
    }

  private val authenticatePasskeyDelegate =
    object : NSObject(), ASAuthorizationControllerDelegateProtocol {
      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization,
      ) {
        val credentialAuthentication = didCompleteWithAuthorization.credential as ASAuthorizationPlatformPublicKeyCredentialAssertion

        val authenticatePasskeyResponse =
          AuthenticatePasskeyResponse(
            id = credentialAuthentication.credentialID.toUrlSafeString(),
            rawId = credentialAuthentication.credentialID.toUrlSafeString(),
            authenticatorAttachment =
              if (deviceUtils.isOSVersionSupported(ATTACHMENT_SUPPORT_MIN_OS_VERSION)) {
                getAuthenticatorAttachment(credentialAuthentication.attachment)
              } else {
                getAuthenticatorAttachment(ASAuthorizationPublicKeyCredentialAttachment.ASAuthorizationPublicKeyCredentialAttachmentPlatform)
              },
            type = PASSKEY_TYPE,
            clientDataJSON = credentialAuthentication.rawClientDataJSON.toUrlSafeString(),
            authenticatorData = credentialAuthentication.rawAuthenticatorData?.toUrlSafeString(),
            signature = credentialAuthentication.signature?.toUrlSafeString(),
            userHandle = credentialAuthentication.userID?.toUrlSafeString(),
          )

        authenticatePasskeyCompletion?.invoke(
          AuthenticatePasskeyResult.Success(
            authenticatePasskeyResponse,
          ),
        )
      }

      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError,
      ) {
        authenticatePasskeyCompletion?.invoke(
          AuthenticatePasskeyResult.Error(
            mapToTwilioException(didCompleteWithError),
          ),
        )
      }
    }

  private fun getAuthenticatorAttachment(attachment: ASAuthorizationPublicKeyCredentialAttachment): String {
    return when (attachment) {
      ASAuthorizationPublicKeyCredentialAttachment.ASAuthorizationPublicKeyCredentialAttachmentCrossPlatform -> Attachment.CROSS_PLATFORM.value
      else -> Attachment.PLATFORM.value
    }
  }

  private fun mapToTwilioException(error: NSError): TwilioException {
    val type =
      when (error.code) {
        PASSKEY_CANCELED_ERROR_CODE -> USER_CANCELED_ERROR
        PASSKEY_INVALID_RESPONSE_ERROR_CODE -> INVALID_RESPONSE_ERROR
        PASSKEY_NOT_HANDLED_ERROR_CODE -> NOT_HANDLED_ERROR
        PASSKEY_FAILED_ERROR_CODE -> FAILED_ERROR
        PASSKEY_NOT_INTERACTIVE_ERROR_CODE -> NOT_INTERACTIVE_ERROR
        else -> UNKNOWN_ERROR
      }
    return TwilioException(type, error.localizedDescription)
  }
}
