//
//  BaseVC.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/15/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit

class BaseVC: UIViewController {
    
    var indicatorView: UIActivityIndicatorView? = nil
    var countIndicator = 0

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func showIndicator() {
        if self.indicatorView == nil {
            self.indicatorView = UIActivityIndicatorView.init(style: UIActivityIndicatorView.Style.gray)
            self.indicatorView?.center = self.view.center
            self.indicatorView?.startAnimating()
            self.view.addSubview(self.indicatorView!)
            UIApplication.shared.beginIgnoringInteractionEvents()
        } else {
            self.view.addSubview(self.indicatorView!)
            self.indicatorView?.startAnimating()
        }
        countIndicator += 1
    }
    
    func hideIndicator() {
        if countIndicator > 1 {
            countIndicator -= 1
        } else {
            if let indicator = self.indicatorView {
                indicator.stopAnimating()
                indicator.removeFromSuperview()
                if countIndicator > 0 {
                    countIndicator -= 1
                }
                if self.indicatorView?.isAnimating == false {
                    UIApplication.shared.endIgnoringInteractionEvents()
                }
                self.indicatorView = nil
            }
            
        }
    }

}
