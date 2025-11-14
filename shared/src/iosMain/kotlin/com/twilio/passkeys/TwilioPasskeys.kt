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

import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationPlatformPublicKeyCredentialProvider
import platform.AuthenticationServices.ASPresentationAnchor
import platform.Foundation.NSData
import platform.Foundation.base64Encoding
import platform.Foundation.create
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume

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
 */
actual open class TwilioPasskeys internal constructor(
  private val passkeyPayloadMapper: PasskeyPayloadMapper = PasskeyPayloadMapper,
  private val authControllerWrapper: IAuthorizationControllerWrapper = AuthorizationControllerWrapper(),
) {
  /**
   * Constructor for creating an instance of [TwilioPasskeys].
   */
  constructor() : this(
    passkeyPayloadMapper = PasskeyPayloadMapper,
    authControllerWrapper = AuthorizationControllerWrapper(),
  )

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
      val publicKeyCredentialProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(createPasskeyRequest.rp.id)
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

      val authController = ASAuthorizationController(authorizationRequests = listOf(registrationRequest))

      authController.setPresentationContextProvider(
        getPresentationContextProvidingProtocol(
          appContext,
        ),
      )

      authControllerWrapper.createPasskey(authController = authController, completion = {
        continuation.resume(it)
      })
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
      val preferImmediatelyAvailableCredentials = authenticatePasskeyRequest.preferImmediatelyAvailableCredentials
      val publicKeyCredentialProvider = ASAuthorizationPlatformPublicKeyCredentialProvider(authenticatePasskeyRequest.publicKey.rpId)
      val challenge = NSData.create(base64Encoding = authenticatePasskeyRequest.publicKey.challenge)
      val assertionRequest = publicKeyCredentialProvider.createCredentialAssertionRequestWithChallenge(challenge = challenge!!)
      val userVerification = authenticatePasskeyRequest.publicKey.userVerification
      assertionRequest.setUserVerificationPreference(userVerification)

      val authController =
        ASAuthorizationController(authorizationRequests = listOf(assertionRequest))

      authController.setPresentationContextProvider(
        getPresentationContextProvidingProtocol(
          appContext,
        ),
      )

      authControllerWrapper.authenticatePasskey(
        authController = authController,
        preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
        completion = { continuation.resume(it) }
      )
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
