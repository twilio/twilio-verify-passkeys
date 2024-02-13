//
//  APIManager.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 22/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

struct Path {
    let url: URL?

    init(
        scheme: String = "https",
        host: String,
        path: String,
        queryItems: [URLQueryItem]? = nil
    ) {
        var components = URLComponents()
        components.scheme = scheme
        components.host = host
        components.path = path
        components.queryItems = queryItems
        url = components.url
    }
}

enum HTTPMethod: String {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case delete = "DELETE"
}


import Foundation

class APIManager {
    static let shared = APIManager()

    private init() {}

    // MARK: - API Request Method

    func request<T: Decodable, U: Encodable>(
        _ url: URL,
        method: HTTPMethod,
        body: U? = nil,
        headers: [String: String]? = nil
    ) async throws -> T {
        try await request(
            url,
            method: method,
            body: try JSONEncoder().encode(body),
            headers: headers
        )
    }

    func request<T: Decodable>(
        _ url: URL,
        method: HTTPMethod,
        body: Data? = nil,
        headers: [String: String]? = nil
    ) async throws -> T {
        var urlRequest = URLRequest(url: url)

        urlRequest.httpMethod = method.rawValue
        if let body = body {
            urlRequest.httpBody = body
            urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        }

        headers?.forEach { key, value in
            urlRequest.addValue(value, forHTTPHeaderField: key)
        }

        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }

        guard 200..<300 ~= httpResponse.statusCode else {
            throw APIError.statusCode(httpResponse.statusCode)
        }

        let decodedData = try JSONDecoder().decode(T.self, from: data)
        return decodedData
    }


    @discardableResult
    func request<U: Encodable>(
        _ url: URL,
        method: HTTPMethod,
        body: U? = nil,
        headers: [String: String]? = nil
    ) async throws -> (data: Data, response: HTTPURLResponse) {
        try await request(
            url,
            method: method,
            body: try JSONEncoder().encode(body),
            headers: headers
        )
    }

    @discardableResult
    func request(
        _ url: URL,
        method: HTTPMethod,
        body: Data? = nil,
        headers: [String: String]? = nil
    ) async throws -> (data: Data, response: HTTPURLResponse) {
        var urlRequest = URLRequest(url: url)

        urlRequest.httpMethod = method.rawValue
        if let body = body {
            urlRequest.httpBody = body
            urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
        }

        headers?.forEach { key, value in
            urlRequest.addValue(value, forHTTPHeaderField: key)
        }

        let (data, response) = try await URLSession.shared.data(for: urlRequest)
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.invalidResponse
        }

        guard 200..<300 ~= httpResponse.statusCode else {
            throw APIError.statusCode(httpResponse.statusCode)
        }

        return (data, httpResponse)
    }
}

// MARK: - API Errors
enum APIError: Error {
    case invalidResponse
    case statusCode(Int)
}

extension Encodable {
    var prettyPrintedJSONString: String? {
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        guard let data = try? encoder.encode(self) else { return nil }
        return String(data: data, encoding: .utf8) ?? nil
    }
}
