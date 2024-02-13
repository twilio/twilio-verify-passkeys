//
//  PasskeysWorker.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 22/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import AuthenticationServices

struct PasskeysWorker {

    // MARK: - Constants

    private enum Operations: String {
        case authenticationStart = "/authentication/start"
        case authenticationVerification = "/authentication/verification"
        case registrationStart = "/registration/start"
        case registrationVerification = "/registration/verification"
    }

    private enum QueryKeys: String {
        case userName = "user_name"
    }

    private enum Errors: Error {
        case invalidPath
        case invalidAttestationObject
    }

    // MARK: - Properties

    let apiManager: APIManager = .shared
    let domain: String

    // MARK: - Public Methods

    // MARK: Registration

    func registrationStart(username: String) async throws -> RegistrationStartResult {
        let request = RegistrationStartRequest(username: username)

        guard let path = Path(
            host: domain,
            path: Operations.registrationStart.rawValue
        ).url else {
            throw Errors.invalidPath
        }

        return try await apiManager.request(
            path,
            method: .post,
            body: request
        )
    }

    func registrationVerification(
        request: RegistrationVerificationRequest
    ) async throws -> VerificationResponse {
        guard let path = Path(host: domain, path: Operations.registrationVerification.rawValue).url else {
            throw Errors.invalidPath
        }

        return try await apiManager.request(
            path,
            method: .post,
            body: request
        )
    }

    // MARK: Authentication

    func authenticationStart() async throws -> VerificationStartResult {
        guard let path = Path(host: domain, path: Operations.authenticationStart.rawValue).url else {
            throw Errors.invalidPath
        }

        return try await apiManager.request(
            path,
            method: .post
        )
    }

    func authenticationVerification(
        request: VerificationRequest
    ) async throws -> VerificationResponse {
        guard let path = Path(host: domain, path: Operations.authenticationVerification.rawValue).url else {
            throw Errors.invalidPath
        }

        return try await apiManager.request(
            path,
            method: .post,
            body: request
        )
    }
}
