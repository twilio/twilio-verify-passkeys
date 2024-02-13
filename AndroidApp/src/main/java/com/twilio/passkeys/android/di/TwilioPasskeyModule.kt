package com.twilio.passkeys.android.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.twilio.passkeys.TwilioPasskey
import com.twilio.passkeys.android.api.AuthenticateApi
import com.twilio.passkeys.android.api.RegistrationApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class TwilioPasskeyModule {
  @Provides
  fun provideTwilioPasskey(
    @ApplicationContext context: Context,
  ): TwilioPasskey {
    return TwilioPasskey(context)
  }

  private val json =
    Json {
      ignoreUnknownKeys = true
      explicitNulls = false
    }

  @Provides
  fun provideRetrofit(): Retrofit {
    val contentType = "application/json".toMediaType()
    val client =
      OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
    return Retrofit.Builder().baseUrl("https://passkey.serveo.net/")
      .addConverterFactory(json.asConverterFactory(contentType)).client(client)
      .build()
  }

  @Provides
  fun provideRegistrationApi(retrofit: Retrofit): RegistrationApi {
    return retrofit.create(RegistrationApi::class.java)
  }

  @Provides
  fun provideAuthenticateApi(retrofit: Retrofit): AuthenticateApi {
    return retrofit.create(AuthenticateApi::class.java)
  }
}
