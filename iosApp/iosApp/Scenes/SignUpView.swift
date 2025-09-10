//
//  SignUpView.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 20/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct SignUpView: View {

    // MARK: - Properties

    @EnvironmentObject private var authenticationManager: AuthenticationManager
    @Binding var username: String
    @State private var showUsernameError: Bool = false

    // MARK: - View

    var body: some View {
        VStack(spacing: .zero) {
            Button {
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                   let window = windowScene.windows.first {
                    Task {
                        do {
                            try await authenticationManager.signIn(on: window)
                        } catch {
                            print(error)
                        }
                    }
                }
            } label: {
                Image("OwlBank")
                    .padding(.bottom, 32)
            }

            VStack(spacing: 8) {
                Text("Welcome to ") + Text("OwlBank").bold()

                Text("What's your username?")
                    .textContentType(.username)
                    .font(.title2)
                    .bold()
            }
            .padding(.bottom, 42)

            VStack(spacing: 16) {
                if showUsernameError {
                    Text("Please enter a valid username (at least 3 characters)")
                        .foregroundColor(.red)
                        .font(.caption)
                }

                HStack {
                    TextField("Username", text: $username)
                        .textContentType(.username)
                        .keyboardType(.default)
                    Image("Twilio")
                }
                .padding(16)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color(red: 0.53, green: 0.57, blue: 0.67), lineWidth: 1)
                )

                Button {
                    // Validate username first
                    if username.isEmpty {
                        showUsernameError = true
                    } else {
                        showUsernameError = false
                        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                           let window = windowScene.windows.first {
                            Task {
                                do {
                                    try await authenticationManager.signUp(as: username, on: window)
                                } catch {
                                    print(error)
                                }
                            }
                        }
                    }
                } label: {
                    Text("Get Started")
                        .bold()
                        .frame(maxWidth: .infinity)
                        .padding(16)
                        .background(
                            RoundedRectangle(cornerRadius: 8).fill(Color("primaryBlue"))
                        )
                        .foregroundColor(.white)
                }
            }

        }
        .padding(.horizontal, 32)
    }
}

struct SignUpView_Previews: PreviewProvider {
    static var previews: some View {
        SignUpView(username: .constant("user123"))
    }
}
