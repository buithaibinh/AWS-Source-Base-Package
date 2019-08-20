//
//  Constant.swift
//  Handmade
//
//  Created by Mai Dang Khoa on 7/1/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import Foundation
import AWSMobileClient
import AWSAppSync

let bucketName = "your_bucket_name"

class MyCognitoUserPoolsAuthProvider : AWSCognitoUserPoolsAuthProviderAsync {
    func getLatestAuthToken(_ callback: @escaping (String?, Error?) -> Void) {
        AWSMobileClient.sharedInstance().getTokens { (tokens, error) in
            if error != nil {
                callback(nil, error)
            } else {
                callback(tokens?.idToken?.tokenString, nil)
            }
        }
    }
}

struct ErrorHandler {
    let code: AWSMobileClientError
    let errorMsg: String
}

func getErrorMsg(_ error: AWSMobileClientError) -> ErrorHandler? {
    switch error {
    case .aliasExists(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .codeDeliveryFailure(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .codeMismatch(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .badRequest(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .cognitoIdentityPoolNotConfigured(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .deviceNotRemembered(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .errorLoadingPage(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .expiredCode(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .expiredRefreshToken(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .federationProviderExists(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .groupExists(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .guestAccessNotAllowed(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .identityIdUnavailable(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .idTokenAndAcceessTokenNotIssued(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .idTokenNotIssued(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .internalError(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .invalidConfiguration(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .invalidLambdaResponse(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .invalidOAuthFlow(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .invalidParameter(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .invalidPassword(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .invalidState(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .limitExceeded(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .mfaMethodNotFound(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .notAuthorized(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .passwordResetRequired(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .resourceNotFound(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .scopeDoesNotExist(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .securityFailed(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .softwareTokenMFANotFound(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .tooManyFailedAttempts(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .tooManyRequests(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .unableToSignIn(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .unexpectedLambda(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .unknown(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .userCancelledSignIn(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .userLambdaValidation(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .usernameExists(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .userNotConfirmed(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .userNotFound(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    case .userPoolNotConfigured(let message):
        return ErrorHandler.init(code: error, errorMsg: message)
    default:
        return nil
    }
}
