package com.twilio.passkeys.utils

import com.twilio.passkeys.mocks.UIDeviceMock
import com.twilio.passkeys.utils.DeviceUtils
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceUtilsTest {
  private val uiDeviceMock = UIDeviceMock()
  private val deviceUtils = DeviceUtils(uiDeviceMock)

  @Test
  fun `Minimum OS version supported is higher than device os version`() {
    uiDeviceMock.mockSystemVersion = "17.0.1"
    assertFalse(deviceUtils.isOSVersionSupported("17.0.2"))
  }

  @Test
  fun `Minimum OS version supported is equal to device os version`() {
    uiDeviceMock.mockSystemVersion = "16.3.1"
    assertTrue(deviceUtils.isOSVersionSupported("16.3.1"))
  }

  @Test
  fun `Minimum OS version supported is lower than device os version`() {
    uiDeviceMock.mockSystemVersion = "16.3.2"
    assertTrue(deviceUtils.isOSVersionSupported("16.3.1"))
  }
}
