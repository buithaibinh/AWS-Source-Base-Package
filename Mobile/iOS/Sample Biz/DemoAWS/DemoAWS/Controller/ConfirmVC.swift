//
//  ConfirmVC.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit

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
                
            }
        }
    }
    
    // MARK: Resend Confirm code
    @IBAction func resendBtnTapped(_ sender: Any) {
        if let username = self.usernameTxt.text {
            if username != "" {
                
            }
        }
    }
}
