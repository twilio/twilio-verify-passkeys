//
//  HomeView.swift
//  iosApp
//
//  Created by Alejandro Orozco Builes on 20/11/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct HomeView: View {

    // MARK: - Properties

    @EnvironmentObject private var authenticationManager: AuthenticationManager
    @State var search: String = ""
    let userId: String

    // MARK: - View

    var body: some View {
        VStack {
            VStack(spacing: .zero) {
                ZStack {
                    Color("primaryBlue")
                        .edgesIgnoringSafeArea(.all)

                    VStack {
                        Button {
                            authenticationManager.signOut()
                        } label: {
                            HStack {
                                Image("OwlWhite")

                                Spacer()

                                HStack(spacing: 23) {
                                    Image("Search")
                                    Image("Menu")
                                }
                            }.padding(.horizontal, 32)
                        }

                        Spacer()

                        HStack {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Hello \(userId),")
                                    .font(.title)
                                    .bold()
                                    .foregroundColor(.white)
                                Text("Welcome back!")
                                    .font(.title3)
                                    .foregroundColor(.white)
                            }
                            Spacer()
                        }.padding(32)

                        HStack {
                            Image("SearchInfo")
                            TextField("", text: $search)
                                .foregroundColor(.white)
                        }
                        .padding(.vertical, 12)
                        .padding(.horizontal, 17)
                        .background(
                            RoundedRectangle(cornerRadius: 22)
                                .fill(.white.opacity(0.15))
                        )
                        .padding(.horizontal, 32)

                        Spacer()
                    }
                }

                ZStack {
                    Color.gray.opacity(0.1)
                        .edgesIgnoringSafeArea(.all)

                    VStack {

                        HStack(spacing: 20) {
                            Image("Copy")
                                .padding(5)
                                .background(Circle().fill(Color.blue).opacity(0.1))

                            Text("Open an account")

                            Spacer()
                        }
                        .frame(maxWidth: .infinity)
                        .padding(20)
                        .background(Rectangle().fill(.white))
                        .padding(.horizontal, 32)

                        HStack(spacing: 20) {
                            Image("CreditCard")
                                .padding(5)
                                .background(Circle().fill(Color.blue).opacity(0.1))

                            Text("Get a credit card")

                            Spacer()
                        }
                        .frame(maxWidth: .infinity)
                        .padding(20)
                        .background(Rectangle().fill(.white))
                        .padding(.horizontal, 32)

                        HStack(spacing: 20) {
                            Image("Loan")
                                .padding(5)
                                .background(Circle().fill(Color.blue).opacity(0.1))

                            Text("Apply for a loan")

                            Spacer()
                        }
                        .frame(maxWidth: .infinity)
                        .padding(20)
                        .background(Rectangle().fill(.white))
                        .padding(.horizontal, 32)
                    }

                    Spacer()
                }
                .padding(.top, -150)
            }
        }
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView(userId: "Jacob")
    }
}
