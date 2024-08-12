//
//  PasskeysModels.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 22/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//
import Foundation

// MARK: - Registration Request

struct RegistrationStartRequest: Codable {
    var username: String
}

struct RegistrationVerificationRequest: Codable {
    let rawId: String
    let id: String
    let clientDataJSON: String
    let attestationObject: String
    let type: String
    let transports: [String]
}

struct RegistrationStartResult: Codable {
    let attestation: String
    let authenticatorSelection: AuthenticatorSelection
    let challenge: String
    let pubKeyCredParams: [PubKeyCredParam]
    let rp: Rp
    let timeout: Int
    let user: UserResult
}

// MARK: - User
struct UserResult: Codable {
    let displayName, id, name: String
}

// MARK: - AuthenticatorSelection
struct AuthenticatorSelection: Codable {
    let authenticatorAttachment: String
    let requireResidentKey: Bool
    let residentKey, userVerification: String
}

// MARK: - PubKeyCredParam
struct PubKeyCredParam: Codable {
    let alg: Int
    let type: String
}

// MARK: - Rp
struct Rp: Codable {
    let id, name: String
}

enum UserStatus: String, Codable {
    case verified, approved
}

struct VerificationResponse: Codable {
    let status: UserStatus
}

// MARK: - VerificationStartResult

struct VerificationStartResult: Codable {
    let publicKey: PublicKey
}

struct PublicKey: Codable {
    let challenge: String
    let timeout: Int
    let rpId, userVerification: String
    let allowCredentials: [String]
}

// MARK: - Verification Request

struct VerificationRequest: Codable {
    var rawId: String
    var id: String
    var clientDataJSON: String
    var userHandle: String?
    var signature: String?
    var authenticatorData: String?
}
