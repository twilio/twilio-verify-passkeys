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

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.twilio.passkeys

import com.twilio.passkeys.exception.TwilioException
import com.twilio.passkeys.exception.UNKNOWN_ERROR
import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.AuthenticatePasskeyResponse
import com.twilio.passkeys.models.CreatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyResponse
import com.twilio.passkeys.utils.DeviceUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialAssertion
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialProvider
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialRegistration
import platform.AuthenticationServices.ASAuthorizationPublicKeyCredentialAttachment
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.base64Encoding
import platform.Foundation.create
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume

internal const val PASSKEY_CANCELED_ERROR_CODE = 1001L
internal const val PASSKEY_INVALID_RESPONSE_ERROR_CODE = 1002L
internal const val PASSKEY_NOT_HANDLED_ERROR_CODE = 1003L
internal const val PASSKEY_FAILED_ERROR_CODE = 1004L
internal const val PASSKEY_NOT_INTERACTIVE_ERROR_CODE = 1005L

internal const val MISSING_ATTESTATION_OBJECT_ERROR = "MISSING_ATTESTATION_OBJECT"
internal const val USER_CANCELED_ERROR = "USER_CANCELED"
internal const val INVALID_RESPONSE_ERROR = "INVALID_RESPONSE"
internal const val NOT_HANDLED_ERROR = "NOT_HANDLED"
internal const val FAILED_ERROR = "FAILED"
internal const val NOT_INTERACTIVE_ERROR = "NOT_INTERACTIVE"

internal const val ATTACHMENT_SUPPORT_MIN_OS_VERSION = "16.6"
internal const val PASSKEY_TYPE = "public-key"

internal enum class Attachment(val value: String) {
  PLATFORM("platform"),
  CROSS_PLATFORM("cross-platform"),
}

/**
 * Represents the Twilio Passkey class responsible for managing passkey operations.
 *
 * @property passkeyPayloadMapper The passkey payload mapper used for mapping passkey payloads and responses.
 * @property deviceUtils The utility class for device-related operations.
 */
actual class TwilioPasskeys private constructor(
  private val passkeyPayloadMapper: PasskeyPayloadMapper = PasskeyPayloadMapper,
  private val deviceUtils: DeviceUtils = DeviceUtils(),
) {
  /**
   * Constructor for creating an instance of [TwilioPasskeys].
   */
  constructor() : this(
    passkeyPayloadMapper = PasskeyPayloadMapper,
  )

  internal var authController: ASAuthorizationController? = null
  internal var createContinuation: (CreatePasskeyResult) -> Unit = {}
  internal var authenticateContinuation: (AuthenticatePasskeyResult) -> Unit = {}

  private val createPasskeyDelegate =
    object : NSObject(), ASAuthorizationControllerDelegateProtocol {
      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization,
      ) {
        val credentialRegistration =
          didCompleteWithAuthorization.credential as ASAuthorizationPlatformPublicKeyCredentialRegistration
        val createPasskeyResponse =
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
            createContinuation(
              CreatePasskeyResult.Error(
                TwilioException("Null attestation object", MISSING_ATTESTATION_OBJECT_ERROR),
              ),
            )
            return
          }

        createContinuation(
          CreatePasskeyResult.Success(
            createPasskeyResponse,
          ),
        )
      }

      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError,
      ) {
        createContinuation(
          CreatePasskeyResult.Error(
            mapToTwilioException(didCompleteWithError),
          ),
        )
      }
    }

  private val authenticatePasskeyDelegate =
    object : NSObject(), ASAuthorizationControllerDelegateProtocol {
      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithAuthorization: ASAuthorization,
      ) {
        val credentialAuthentication =
          didCompleteWithAuthorization.credential as ASAuthorizationPlatformPublicKeyCredentialAssertion
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
        authenticateContinuation(
          AuthenticatePasskeyResult.Success(
            authenticatePasskeyResponse,
          ),
        )
      }

      override fun authorizationController(
        controller: ASAuthorizationController,
        didCompleteWithError: NSError,
      ) {
        authenticateContinuation(
          AuthenticatePasskeyResult.Error(
            mapToTwilioException(didCompleteWithError),
          ),
        )
      }
    }

  private fun getPresentationContextProvidingProtocol(appContext: AppContext): ASAuthorizationControllerPresentationContextProvidingProtocol {
    return object : NSObject(), ASAuthorizationControllerPresentationContextProvidingProtocol {
      override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): ASPresentationAnchor {
        return appContext.uiWindow
      }
    }
  }

  /**
   * Creates a passkey using the provided [createPasskeyRequest] and [appContext].
   *
   * @param createPasskeyRequest The request for creating the passkey.
   * @param appContext The [UIWindow] context.
   * @return The result of creating the passkey.
   */
  actual suspend fun create(
    createPasskeyRequest: CreatePasskeyRequest,
    appContext: AppContext,
  ): CreatePasskeyResult =
    suspendCancellableCoroutine { continuation ->
      val publicKeyCredentialProvider =
        ASAuthorizationPlatformPublicKeyCredentialProvider(createPasskeyRequest.rp.id)
      val challenge = NSData.create(base64Encoding = createPasskeyRequest.challenge)
      val userID = NSData.create(base64Encoding = createPasskeyRequest.user.id)
      val registrationRequest =
        publicKeyCredentialProvider.createCredentialRegistrationRequestWithChallenge(
          challenge = challenge!!,
          name = createPasskeyRequest.user.name,
          userID = userID!!,
        )

      createPasskeyRequest.attestation?.let {
        registrationRequest.setAttestationPreference(it)
      }
      authController =
        ASAuthorizationController(authorizationRequests = listOf(registrationRequest))
      authController?.delegate = createPasskeyDelegate
      authController?.setPresentationContextProvider(
        getPresentationContextProvidingProtocol(
          appContext,
        ),
      )
      authController?.performRequests()
      createContinuation = {
        continuation.resume(it)
      }
    }

  /**
   * Creates a passkey using the provided [createPayload] and [appContext].
   *
   * @param createPayload The payload for creating the passkey.
   * @param appContext The [UIWindow] context.
   * @return The result of creating the passkey.
   */
  actual suspend fun create(
    createPayload: String,
    appContext: AppContext,
  ): CreatePasskeyResult {
    return try {
      val createPasskeyRequest =
        passkeyPayloadMapper.mapToCreatePasskeyRequest(createPayload)
      create(createPasskeyRequest, appContext)
    } catch (e: Exception) {
      CreatePasskeyResult.Error(passkeyPayloadMapper.mapException(e))
    }
  }

  /**
   * Authenticates a passkey using the provided [authenticatePasskeyRequest] and [appContext].
   *
   * @param authenticatePasskeyRequest The request for authenticating the passkey.
   * @param appContext The [UIWindow] context.
   * @return The result of authenticating the passkey.
   */
  actual suspend fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
    appContext: AppContext,
  ): AuthenticatePasskeyResult =
    suspendCancellableCoroutine { continuation ->
      val publicKeyCredentialProvider =
        ASAuthorizationPlatformPublicKeyCredentialProvider(authenticatePasskeyRequest.publicKey.rpId)
      val challenge = NSData.create(base64Encoding = authenticatePasskeyRequest.publicKey.challenge)

      val assertionRequest =
        publicKeyCredentialProvider.createCredentialAssertionRequestWithChallenge(challenge = challenge!!)
      val userVerification = authenticatePasskeyRequest.publicKey.userVerification
      assertionRequest.setUserVerificationPreference(userVerification)

      authController =
        ASAuthorizationController(authorizationRequests = listOf(assertionRequest))
      authController?.delegate = authenticatePasskeyDelegate
      authController?.setPresentationContextProvider(
        getPresentationContextProvidingProtocol(
          appContext,
        ),
      )
      authController?.performRequests()
      authenticateContinuation = {
        continuation.resume(it)
      }
    }

  /**
   * Authenticates a passkey using the provided [authenticatePayload] and [appContext].
   *
   * @param authenticatePayload The payload for authenticating the passkey.
   * @param appContext The [UIWindow] context.
   * @return The result of authenticating the passkey.
   */
  actual suspend fun authenticate(
    authenticatePayload: String,
    appContext: AppContext,
  ): AuthenticatePasskeyResult {
    return try {
      val authenticatePasskeyRequest =
        passkeyPayloadMapper.mapToAuthenticatePasskeyRequest(authenticatePayload)
      authenticate(authenticatePasskeyRequest, appContext)
    } catch (e: Exception) {
      AuthenticatePasskeyResult.Error(passkeyPayloadMapper.mapException(e))
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

  private fun getAuthenticatorAttachment(attachment: ASAuthorizationPublicKeyCredentialAttachment): String {
    return when (attachment) {
      ASAuthorizationPublicKeyCredentialAttachment.ASAuthorizationPublicKeyCredentialAttachmentCrossPlatform -> Attachment.CROSS_PLATFORM.value
      else -> Attachment.PLATFORM.value
    }
  }
}

/**
 * Converts NSData to a URL-safe string.
 *
 * @receiver The NSData to be converted.
 * @return The URL-safe string representation of the NSData.
 */
internal fun NSData.toUrlSafeString(): String =
  this.base64Encoding().replace("+", "-")
    .replace("/", "_").replace("=", "")

/**
 * Represents the UI window context.
 *
 * @property uiWindow The active UIWindow where the Passkey Dialogs will be presented.
 */
actual open class AppContext(open val uiWindow: UIWindow)
