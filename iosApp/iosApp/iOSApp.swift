import SwiftUI

@main
struct iOSApp: App {
    @State var phoneNumber: String = ""
    @StateObject private var authenticationManager = AuthenticationManager()

	var body: some Scene {
		WindowGroup {
            LandingView(authenticationManager: authenticationManager)
		}
	}
}
