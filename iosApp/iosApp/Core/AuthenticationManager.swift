//
//  AuthenticationManager.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 20/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import TwilioPasskeys
import Combine
import UIKit

public enum User {
    case authenticated(username: String)

    public init(username: String) {
        self = .authenticated(username: username)
    }
}

let domain: String = "passkey-sample-9652.twil.io"

class AuthenticationManager: NSObject, ObservableObject {

    // MARK: - Properties

    private let worker: PasskeysWorker = .init(domain: domain)
    private let twilioPasskey = TwilioPasskey()
    @Published public var currentUser: User? = nil

    var isSignedIn: Bool {
        currentUser != nil
    }

    // MARK: - Public Methods

    func signIn(
        on window: UIWindow
    ) async throws {
        let result = try await worker.authenticationStart()
        let data = try JSONEncoder().encode(result)
        let json = String(data: data, encoding: .utf8)!
        print(json)
        
        let response = try await twilioPasskey.authenticate(challengePayload: json, appContext: AppContext(uiWindow: window))
        if let success = response as? AuthenticatePasskeyResult.Success {
                finishSignIn(with: success.authenticatePasskeyResponse)
        } else if let error = response as? AuthenticatePasskeyResult.Error {
            print(error)
        }
    }


    func signUp(
        as username: String,
        on window: UIWindow
    ) async throws {
        let result = try await worker.registrationStart(username: username)
        let data = try JSONEncoder().encode(result)
        let json = String(data: data, encoding: .utf8)!
        print(json)

        let response = try await twilioPasskey.create(challengePayload: json, appContext: AppContext(uiWindow: window))
        if let success = response as? CreatePasskeyResult.Success {
            finishSignUp(
                for: username,
                with: success.createPasskeyResponse
            )
        } else if let error = response as? CreatePasskeyResult.Error {
            print(error)
        }
    }

    func signOut() {
        currentUser = nil
    }

    // MARK: - Private Methods

    private func finishSignUp(
        for userName: String,
        with response: CreatePasskeyResponse
    ) {
        Task {
            do {
                let result = try await worker.registrationVerification(request: .init(
                    rawId: response.rawId,
                    id: response.id,
                    clientDataJson: response.clientDataJSON,
                    attestationObject: response.attestationObject,
                    type: response.type,
                    transports: ["internal"])
                )

                await MainActor.run {
                    currentUser = .authenticated(username: userName)
                }
            } catch {
                print(error)
            }
        }
    }

    private func finishSignIn(
        with response: AuthenticatePasskeyResponse
    ) {
        Task {
            do {
                _ = try await worker.authenticationVerification(request: .init(
                    rawId: response.rawId,
                    id: response.id,
                    clientDataJson: response.clientDataJSON,
                    userHandle: response.userHandle,
                    signature: response.signature,
                    authenticatorData: response.authenticatorData
                ))

                await MainActor.run {
                    currentUser = .authenticated(username: response.id)
                }
            } catch {
                print(error)
            }
        }
    }
}
