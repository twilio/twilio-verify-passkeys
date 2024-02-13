import SwiftUI

@main
struct iOSApp: App {
    @State var phoneNumber: String = ""
    @StateObject private var authenticationManger = AuthenticationManager()

	var body: some Scene {
		WindowGroup {
            LandingView(authenticationManger: authenticationManger)
		}
	}
}
