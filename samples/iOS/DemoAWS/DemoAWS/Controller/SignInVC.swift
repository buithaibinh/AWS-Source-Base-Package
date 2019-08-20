//
//  SignInVC.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import AWSMobileClient

class SignInVC: BaseVC {

    @IBOutlet weak var usernameTxt: UITextField!
    @IBOutlet weak var passwordTxt: UITextField!
    let delegate = UIApplication.shared.delegate as! AppDelegate
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        //Check UserState First Time
        if delegate.isFirstTime {
            self.showIndicator()
            self.delegate.checkUserState { (userState) in
                if userState == .signedIn {
                    AWSMobileClient.sharedInstance().getTokens({ (token, error) in
                        DispatchQueue.main.async {
                            self.hideIndicator()
                            if let error = error as? AWSMobileClientError {
                                let alert = UIAlertController(title: "Error",
                                                              message: getErrorMsg(error)?.errorMsg,
                                                              preferredStyle: .alert)
                                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                                
                                self.present(alert, animated: true, completion:nil)
                            } else {
                                if let token = token, let idToken = token.idToken {
                                    if idToken.tokenString == nil {
                                        AWSMobileClient.sharedInstance().signOut()
                                    } else {
                                        self.performSegue(withIdentifier: "MOVE_TO_HOME", sender: idToken.claims)
                                    }
                                }
                            }
                        }
                    })
                } else {
                    DispatchQueue.main.async {
                        self.hideIndicator()
                    }
                }
            }
            delegate.isFirstTime = false
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let vc = segue.destination as? HomeVC {
            if let dict = sender as? [String: Any] {
                vc.user = dict
            }
        }
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.usernameTxt.text = nil
        self.passwordTxt.text = nil
    }
    
    // MARK: Sign In with Facebook
    @IBAction func signInByFB(_ sender: Any) {
        self.showIndicator()
        let hostedUIOptions = HostedUIOptions(scopes: ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"], identityProvider: "Facebook")
        // Present the Hosted UI sign in.
        AWSMobileClient.sharedInstance().showSignIn(navigationController: self.navigationController!, hostedUIOptions: hostedUIOptions) { (userState, error) in
            DispatchQueue.main.async {
                self.hideIndicator()
                if let error = error as? AWSMobileClientError {
                    let alert = UIAlertController(title: "Error",
                                                  message: getErrorMsg(error)?.errorMsg,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    
                    self.present(alert, animated: true, completion:nil)
                }
                if let userState = userState {
                    if userState == .signedIn {
                        AWSMobileClient.sharedInstance().getTokens({ (token, error) in
                            if let error = error as? AWSMobileClientError {
                                let alert = UIAlertController(title: "Error",
                                                              message: getErrorMsg(error)?.errorMsg,
                                                              preferredStyle: .alert)
                                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                                
                                self.present(alert, animated: true, completion:nil)
                            } else {
                                DispatchQueue.main.async {
                                    self.performSegue(withIdentifier: "MOVE_TO_HOME", sender: token?.idToken?.claims)
                                }
                            }
                        })
                    }
                }
            }
        }
    }
    
    // MARK: Sign In with Google
    @IBAction func signInByGG(_ sender: Any) {
        self.showIndicator()
        let hostedUIOptions = HostedUIOptions(scopes: ["phone", "email", "openid", "profile", "aws.cognito.signin.user.admin"], identityProvider: "Google")
        // Present the Hosted UI sign in.
        AWSMobileClient.sharedInstance().showSignIn(navigationController: self.navigationController!, hostedUIOptions: hostedUIOptions) { (userState, error) in
            DispatchQueue.main.async {
                self.hideIndicator()
                if let error = error as? AWSMobileClientError {
                    let alert = UIAlertController(title: "Error",
                                                  message: getErrorMsg(error)?.errorMsg,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    
                    self.present(alert, animated: true, completion:nil)
                }
                if let userState = userState {
                    if userState == .signedIn {
                        AWSMobileClient.sharedInstance().getTokens({ (token, error) in
                            if let error = error as? AWSMobileClientError {
                                let alert = UIAlertController(title: "Error",
                                                              message: getErrorMsg(error)?.errorMsg,
                                                              preferredStyle: .alert)
                                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                                
                                self.present(alert, animated: true, completion:nil)
                            } else {
                                DispatchQueue.main.async {
                                    self.performSegue(withIdentifier: "MOVE_TO_HOME", sender: token?.idToken?.claims)
                                }
                            }
                        })
                    }
                }
            }
        }
    }
    
    // MARK: Sign In with Email, Password
    @IBAction func signInBtnTapped(_ sender: Any) {
        if let username = self.usernameTxt.text, let password = self.passwordTxt.text {
            if username != "" && password != "" {
                self.showIndicator()
                AWSMobileClient.sharedInstance().signIn(username: username, password: password) { (result, error) in
                    DispatchQueue.main.async {
                        self.hideIndicator()
                        if let error = error as? AWSMobileClientError {
                            let alert = UIAlertController(title: "Error",
                                                          message: getErrorMsg(error)?.errorMsg,
                                                          preferredStyle: .alert)
                            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                            
                            self.present(alert, animated: true, completion:nil)
                        } else if let _ = result {
                            AWSMobileClient.sharedInstance().getTokens({ (token, error) in
                                if let error = error as? AWSMobileClientError {
                                    let alert = UIAlertController(title: "Error",
                                                                  message: getErrorMsg(error)?.errorMsg,
                                                                  preferredStyle: .alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                                    
                                    self.present(alert, animated: true, completion:nil)
                                } else {
                                    DispatchQueue.main.async {
                                        self.performSegue(withIdentifier: "MOVE_TO_HOME", sender: token?.idToken?.claims)
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
    
    @IBAction func signUpBtnTapped(_ sender: Any) {
        self.performSegue(withIdentifier: "MOVE_TO_SIGNUP", sender: nil)
    }
}
