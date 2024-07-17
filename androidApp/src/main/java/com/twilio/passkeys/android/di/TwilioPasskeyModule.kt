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

package com.twilio.passkeys.android.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.twilio.passkeys.TwilioPasskey
import com.twilio.passkeys.android.BuildConfig
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
  fun provideTwilioPasskey(): TwilioPasskey {
    return TwilioPasskey()
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
    return Retrofit.Builder().baseUrl(BuildConfig.SAMPLE_BACKEND_URL)
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
