package com.twilio.passkeys.extensions

import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.CreateCredentialUnsupportedException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.domerrors.AbortError
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialDomException
import androidx.credentials.exceptions.publickeycredential.GetPublicKeyCredentialException
import com.google.common.truth.Truth.assertThat
import com.twilio.passkeys.exception.TwilioException
import io.mockk.mockk
import org.junit.Test

class CredentialExceptionExtensionTest {
  @Test
  fun `GetCredentialException maps to UserCanceledException`() {
    val exception = GetCredentialCancellationException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.UserCanceledException).isTrue()
  }

  @Test
  fun `GetCredentialException maps to InterruptedException`() {
    val exception = GetCredentialInterruptedException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.InterruptedException).isTrue()
  }

  @Test
  fun `GetCredentialException maps to UnsupportedException`() {
    val exception = GetCredentialUnsupportedException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.UnsupportedException).isTrue()
  }

  @Test
  fun `GetPublicKeyCredentialDomException maps to DomException`() {
    val exception = GetPublicKeyCredentialDomException(AbortError(), "error")
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.DomException).isTrue()
    assertThat((result as TwilioException.DomException).message == DOM_MESSAGE.trimIndent()).isTrue()
  }

  @Test
  fun `NoCredentialException maps to NoCredentialException`() {
    val exception = NoCredentialException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.NoCredentialException).isTrue()
  }

  @Test
  fun `GetPublicKeyCredentialException maps to GeneralException`() {
    val exception = mockk<GetPublicKeyCredentialException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `GetCredentialUnknownException maps to GeneralException`() {
    val exception = GetCredentialUnknownException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `GetCredentialCustomException maps to GeneralException`() {
    val exception = mockk<GetCredentialCustomException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `GetCredentialProviderConfigurationException maps to GeneralException`() {
    val exception = mockk<GetCredentialProviderConfigurationException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `CreateCredentialException maps to UserCanceledException`() {
    val exception = CreateCredentialCancellationException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.UserCanceledException).isTrue()
  }

  @Test
  fun `CreateCredentialException maps to InterruptedException`() {
    val exception = CreateCredentialInterruptedException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.InterruptedException).isTrue()
  }

  @Test
  fun `CreateCredentialException maps to UnsupportedException`() {
    val exception = CreateCredentialUnsupportedException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.UnsupportedException).isTrue()
  }

  @Test
  fun `CreatePublicKeyCredentialDomException maps to DomException`() {
    val exception = CreatePublicKeyCredentialDomException(AbortError(), "error")
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.DomException).isTrue()
    assertThat((result as TwilioException.DomException).message == DOM_MESSAGE.trimIndent()).isTrue()
  }

  @Test
  fun `CreateCredentialNoCreateOptionException maps to GeneralException`() {
    val exception = CreateCredentialNoCreateOptionException()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `CreatePublicKeyCredentialException maps to GeneralException`() {
    val exception = mockk<CreatePublicKeyCredentialException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `CreateCredentialUnknownException maps to GeneralException`() {
    val exception = mockk<CreateCredentialUnknownException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `CreateCredentialCustomException maps to GeneralException`() {
    val exception = mockk<CreateCredentialCustomException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }

  @Test
  fun `CreateCredentialProviderConfigurationException maps to GeneralException`() {
    val exception = mockk<CreateCredentialProviderConfigurationException>()
    val result = exception.toTwilioException()
    assertThat(result is TwilioException.GeneralException).isTrue()
  }
}
