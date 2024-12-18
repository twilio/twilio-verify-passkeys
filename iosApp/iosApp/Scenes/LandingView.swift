//
//  LandingView.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 20/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct LandingView: View {

    // MARK: - Properties

    @State var phoneNumber: String = .init()
    @ObservedObject var authenticationManager: AuthenticationManager

    // MARK: - Private Methods

    @ViewBuilder
    private func TabDemo(name: String, icon: String) -> some View {
        Text(name)
            .tabItem {
                Label(name, image: icon)
            }
    }

    // MARK: - View

    var body: some View {
        if case let .authenticated(username) = authenticationManager.currentUser {
            TabView {
                HomeView(userId: username)
                    .environmentObject(authenticationManager)
                    .onAppear {
                        phoneNumber = .init()
                    }
                    .tabItem {
                        Label("Accounts", image: "Accounts")
                    }

                TabDemo(name: "Transfer", icon: "Transfer")
                TabDemo(name: "Bill Pay", icon: "BillPay")
                TabDemo(name: "Deposit", icon: "Deposit")
                TabDemo(name: "More", icon: "More")
            }
        } else {
            SignUpView(phoneNumber: $phoneNumber)
                .environmentObject(authenticationManager)
        }
    }
}

struct Previews_LandingView_Previews: PreviewProvider {
    static var previews: some View {
        LandingView(authenticationManager: .init())
    }
}
