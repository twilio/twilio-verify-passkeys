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
    @Binding var phoneNumber: String

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

                Text("What's your phone number?")
                    .textContentType(.name)
                    .font(.title2)
                    .bold()
            }
            .padding(.bottom, 42)

            VStack(spacing: 16) {

                HStack {
                    TextField("Phone number", text: $phoneNumber)
                        .textContentType(.telephoneNumber)
                        .keyboardType(.phonePad)
                    Image("Twilio")
                }
                .padding(16)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color(red: 0.53, green: 0.57, blue: 0.67), lineWidth: 1)
                )

                Button {
                    if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                       let window = windowScene.windows.first {
                        Task {
                            do {
                                try await authenticationManager.signUp(as: phoneNumber, on: window)
                            } catch {
                                print(error)
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
        SignUpView(phoneNumber: .constant("123456"))
    }
}
