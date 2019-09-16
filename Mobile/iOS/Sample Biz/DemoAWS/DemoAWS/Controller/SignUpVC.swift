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
                
            }
        }
    }
}
