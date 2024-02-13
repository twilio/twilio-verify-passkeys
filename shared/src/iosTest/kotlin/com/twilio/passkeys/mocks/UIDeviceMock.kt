package com.twilio.passkeys.mocks

import platform.UIKit.UIDevice

class UIDeviceMock : UIDevice() {
  var mockSystemVersion: String? = null

  override fun systemVersion(): String {
    return mockSystemVersion ?: "0.0.0"
  }
}
