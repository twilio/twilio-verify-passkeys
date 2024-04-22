
package com.twilio.passkeys

import com.twilio.passkeys.models.AuthenticatePasskeyRequest
import com.twilio.passkeys.models.CreatePasskeyRequest

suspend fun nativeCreate(
  challengePayload: String,
): CreatePasskeyRequest {
  return PasskeyPayloadMapper.mapToPasskeyCreationPayload(challengePayload)
}


@JsModule("./create")
@JsNonModule
@JsName("createJs")
external fun createJs(username: String, domain: String): String

@JsModule("./authenticate")
@JsNonModule
@JsName("authenticateJs")
external fun authenticateJs(domain: String): String

@JsExport
@JsName("TwilioPasskey")
class TwilioPasskeyClass {
  fun create(username: String, domain: String) {
    val status = createJs(username, domain);
    console.log(status)
  }

  fun authenticate(domain: String) {
    val status = authenticateJs(domain)
    console.log(status)
  }
}


actual class TwilioPasskey private constructor(
  private val passkeysPayloadMapper: PasskeyPayloadMapper
) {
  constructor() : this(
    passkeysPayloadMapper = PasskeyPayloadMapper
  )

  actual suspend fun create(
    createPasskeyRequest: CreatePasskeyRequest,
    appContext: AppContext
  ): CreatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun create(challengePayload: String, appContext: AppContext): CreatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun authenticate(
    authenticatePasskeyRequest: AuthenticatePasskeyRequest,
    appContext: AppContext
  ): AuthenticatePasskeyResult {
    TODO("Not yet implemented")
  }

  actual suspend fun authenticate(
    challengePayload: String,
    appContext: AppContext
  ): AuthenticatePasskeyResult {
    TODO("Not yet implemented")
  }
}

actual class AppContext()

@JsExport
@JsName("hello")
fun hello() {
  console.log("Hello Kotlin/JS")
}
