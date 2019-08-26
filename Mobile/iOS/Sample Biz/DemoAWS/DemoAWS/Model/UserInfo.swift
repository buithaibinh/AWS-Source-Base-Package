//
//  UserInfo.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import Foundation

class UserInfo {
    var username = ""
    var nonce = ""
    var auth_time = ""
    var aud = ""
    var at_hash = ""
    var iss = ""
    var sub = ""
    var iat = 0
    var token_use = ""
    var identities: Identity? = nil
    var groups = [String]()
    var exp = 0
    var email = ""
    var picture = ""
    
    init(dict: [String: Any]) {
        self.username = dict["cognito:username"] as? String ?? ""
        self.nonce = dict["nonce"] as? String ?? ""
        self.auth_time = dict["auth_time"] as? String ?? ""
        self.aud = dict["aud"] as? String ?? ""
        self.at_hash = dict["at_hash"] as? String ?? ""
        self.iss = dict["iss"] as? String ?? ""
        self.sub = dict["sub"] as? String ?? ""
        self.iat = dict["iat"] as? Int ?? 0
        self.token_use = dict["token_use"] as? String ?? ""
        self.identities = Identity.init(dict: dict["identities"] as? [String: Any] ?? nil)
        self.groups = dict["cognito:groups"] as? [String] ?? []
        self.exp = dict["exp"] as? Int ?? 0
        self.email = dict["email"] as? String ?? ""
        self.picture = dict["picture"] as? String ?? ""
    }
    
}

class Identity {
    var dateCreated = ""
    var issue = ""
    var primary = ""
    var providerName = ""
    var providerType = ""
    var userId = ""
    init(dict: [String: Any]?) {
        if let dict = dict {
            self.dateCreated = dict["dateCreated"] as? String ?? ""
            self.issue = dict["issue"] as? String ?? ""
            self.primary = dict["primary"] as? String ?? ""
            self.providerName = dict["providerName"] as? String ?? ""
            self.providerType = dict["providerType"] as? String ?? ""
            self.userId = dict["userId"] as? String ?? ""
        }
    }
}

class UserDetails {
    var sub = ""
    var email = ""
    var phoneNumber = ""
    var picture = ""
    
    init(attributes: [AllUsersQuery.Data.AllUser.Item.Attribute]) {
        for attribute in attributes {
            switch attribute.name {
            case "sub":
                self.sub = attribute.value
                break
            case "email":
                self.email = attribute.value
                break
            case "phone_number":
                self.phoneNumber = attribute.value
                break
            case "picture":
                self.picture = attribute.value
                break
            default:
                break
            }
        }
    }
    
    init(dict: [String: String]) {
        self.sub = dict["sub"] ?? ""
        self.email = dict["email"] ?? ""
        self.phoneNumber = dict["phoneNumber"] ?? ""
        self.picture = dict["picture"] ?? ""
    }
    
}
