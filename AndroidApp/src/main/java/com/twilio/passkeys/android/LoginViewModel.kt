package com.twilio.passkeys.android

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twilio.passkeys.AppContext
import com.twilio.passkeys.AuthenticatePasskeyResult
import com.twilio.passkeys.CreatePasskeyResult
import com.twilio.passkeys.TwilioPasskey
import com.twilio.passkeys.android.model.RegistrationStartResponse
import com.twilio.passkeys.android.repository.AuthenticateRepository
import com.twilio.passkeys.android.repository.CreateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
  private val twilioPasskey: TwilioPasskey,
  private val authenticateRepository: AuthenticateRepository,
  private val createRepository: CreateRepository,
) : ViewModel() {
  private val _state: MutableSharedFlow<LoginState> =
    MutableSharedFlow(
      replay = 1,
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
  val state = _state.asSharedFlow()

  fun create(
    username: String,
    activity: Activity,
  ) {
    viewModelScope.launch {
      try {
        val registrationStartResponse: RegistrationStartResponse =
          createRepository.start(
            username,
          )
        val json =
          Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            explicitNulls = false
          }
        val challengePayload = json.encodeToString(registrationStartResponse)
println("felo challengePayload: $challengePayload")
        when (val createPasskeyResult = twilioPasskey.create(challengePayload, AppContext(activity))) {
          is CreatePasskeyResult.Error -> {
            setErrorState(createPasskeyResult.error.message.toString())
          }

          is CreatePasskeyResult.Success -> {
            try {
              createRepository.verification(
                rawId = createPasskeyResult.createPasskeyResponse.rawId,
                id = createPasskeyResult.createPasskeyResponse.id,
                clientDataJson = createPasskeyResult.createPasskeyResponse.clientDataJSON,
                attestationObject = createPasskeyResult.createPasskeyResponse.attestationObject,
                type = createPasskeyResult.createPasskeyResponse.type,
                transports = createPasskeyResult.createPasskeyResponse.transports,
              )
              _state.emit(LoginState.PasskeySuccess(username))
            } catch (e: Exception) {
              setErrorState(e.message.toString())
            }
          }
        }
      } catch (e: Exception) {
        setErrorState(e.message.toString())
      }
    }
  }

  fun authenticate(activity: Activity) {
    viewModelScope.launch {
      try {
        val authenticationStartResponse = authenticateRepository.start()
        val json = Json { encodeDefaults = true }
        val challengePayload = json.encodeToString(authenticationStartResponse)
        println("felo challengePayload: $challengePayload")
        val authenticatePasskeyResult = twilioPasskey.authenticate(challengePayload, AppContext(activity))
        println("felo authenticatePasskeyResult: $authenticatePasskeyResult")
        when (authenticatePasskeyResult) {
          is AuthenticatePasskeyResult.Error -> {

            setErrorState(
              authenticatePasskeyResult.error.message,
            )
          }

          is AuthenticatePasskeyResult.Success -> {
            try {
              val response =
                authenticatePasskeyResult.authenticatePasskeyResponse
              authenticateRepository.verification(
                rawId = response.rawId,
                id = response.id,
                clientDataJson = response.clientDataJSON,
                userHandle = response.userHandle,
                signature = response.signature,
                authenticatorData = response.authenticatorData,
              )
              _state.emit(
                LoginState.PasskeySuccess(
                  "test",
                ),
              )
            } catch (e: Exception) {
              setErrorState(e.message.toString())
            }
          }
        }
      } catch (e: Exception) {
        setErrorState(e.message.toString())
      }
    }
  }

  fun logout() {
    viewModelScope.launch {
      _state.emit(LoginState.Logout)
    }
  }

  fun areFieldsValid(phoneNumber: String): Boolean {
    if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
      _state.tryEmit(LoginState.NumberError)
      return false
    }
    return true
  }

  private fun setErrorState(message: String) {
    _state.tryEmit(
      LoginState.PasskeyError(
        message,
      ),
    )
  }
}

sealed interface LoginState {
  data object Initial : LoginState

  data object NumberError : LoginState

  data object Logout : LoginState

  data class PasskeySuccess(val number: String) : LoginState

  data class PasskeyError(val message: String) : LoginState
}
