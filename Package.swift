// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "PasskeyTestIOS",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "PasskeyTestIOS",
            targets: ["PasskeyTestIOS"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "PasskeyTestIOS",
            path: "./PasskeyTestIOS.xcframework"
        ),
    ]
)
