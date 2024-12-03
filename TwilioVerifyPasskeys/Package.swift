// swift-tools-version: 6.0
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
   name: "TwilioVerifyPasskeys",
   platforms: [
     .iOS(.v14),
   ],
   products: [
      .library(name: "TwilioVerifyPasskeys", targets: ["TwilioVerifyPasskeys"])
   ],
   targets: [
      .binaryTarget(
         name: "TwilioVerifyPasskeys",
         url: "https://github.com/twilio/twilio-verify-passkeys/releases/download/0.3.0-ios/TwilioPasskeysAuthentication.xcframework.zip",
         checksum: "78759764fe8b573f81a8673e533174acd2eea3b844fdaf2b806aec8f525ab265")
   ]
)
