//
//  ConfirmVC.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import AWSMobileClient

class ConfirmVC: BaseVC {

    @IBOutlet weak var usernameTxt: UITextField!
    @IBOutlet weak var confirmcodeTxt: UITextField!
    var username = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Confirmation"
        self.usernameTxt.text = self.username
    }
    
    // MARK: Confirm code
    @IBAction func confirmBtnTapped(_ sender: Any) {
        if let confirmCode = self.confirmcodeTxt.text, let username = self.usernameTxt.text {
            if confirmCode != "" && username != "" {
                self.showIndicator()
                AWSMobileClient.sharedInstance().confirmSignUp(username: username, confirmationCode: confirmCode) { (result, error) in
                    DispatchQueue.main.async {
                        self.hideIndicator()
                        if let error = error as? AWSMobileClientError {
                            let alert = UIAlertController(title: "Error",
                                                          message: getErrorMsg(error)?.errorMsg,
                                                          preferredStyle: .alert)
                            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                            self.present(alert, animated: true, completion:nil)
                        } else {
                            if let _ = result {
                                self.navigationController?.popToRootViewController(animated: true)
                            }
                        }
                    }
                }
            }
        }
    }
    
    // MARK: Resend Confirm code
    @IBAction func resendBtnTapped(_ sender: Any) {
        if let username = self.usernameTxt.text {
            if username != "" {
                self.showIndicator()
                AWSMobileClient.sharedInstance().resendSignUpCode(username: username) { (result, error) in
                    DispatchQueue.main.async {
                        self.hideIndicator()
                        if let error = error as? AWSMobileClientError {
                            let alert = UIAlertController(title: "Error",
                                                          message: getErrorMsg(error)?.errorMsg,
                                                          preferredStyle: .alert)
                            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                            self.present(alert, animated: true, completion:nil)
                        } else {
                            if let _ = result {
                                let alert = UIAlertController(title: "Success",
                                                              message: "Resend Complete",
                                                              preferredStyle: .alert)
                                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                                self.present(alert, animated: true, completion:nil)
                            }
                        }
                    }
                }
            }
        }
    }
}
