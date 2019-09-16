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
    }
    
    // MARK: Sign In with Google
    @IBAction func signInByGG(_ sender: Any) {
        
    }
    
    // MARK: Sign In with Email, Password
    @IBAction func signInBtnTapped(_ sender: Any) {
        if let username = self.usernameTxt.text, let password = self.passwordTxt.text {
            if username != "" && password != "" {
            }
        }
    }
    
    @IBAction func signUpBtnTapped(_ sender: Any) {
        self.performSegue(withIdentifier: "MOVE_TO_SIGNUP", sender: nil)
    }
}
