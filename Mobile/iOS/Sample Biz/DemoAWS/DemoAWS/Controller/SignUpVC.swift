//
//  SignUpVC.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import AWSMobileClient

class SignUpVC: BaseVC {

    @IBOutlet weak var usernameTxt: UITextField!
    @IBOutlet weak var passwordTxt: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Sign Up"
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let vc = segue.destination as? ConfirmVC {
            if let username = sender as? String {
                vc.username = username
            }
        }
    }
    
    // MARK: Sign Up By Email, Password
    @IBAction func signupBtnTapped(_ sender: Any) {
        if let username = self.usernameTxt.text, let password = self.passwordTxt.text {
            if username != "" && password != "" {
                self.showIndicator()
                AWSMobileClient.sharedInstance().signUp(username: username, password: password, userAttributes: [:], validationData: [:]) { (result, error) in
                    DispatchQueue.main.async {
                        self.hideIndicator()
                        if let error = error as? AWSMobileClientError {
                            let alert = UIAlertController(title: "Error",
                                                          message: getErrorMsg(error)?.errorMsg,
                                                          preferredStyle: .alert)
                            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                            self.present(alert, animated: true, completion:nil)
                        } else if let result = result {
                            if (result.signUpConfirmationState != .confirmed) {
                                self.performSegue(withIdentifier: "MOVE_TO_CONFIRM", sender: username)
                            }
                        }
                    }
                }
            }
        }
        
    }
}
