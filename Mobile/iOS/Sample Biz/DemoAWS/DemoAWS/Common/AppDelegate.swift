//
//  AppDelegate.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import AWSMobileClient
import AWSS3
import SDWebImage

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var isFirstTime = false

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        self.isFirstTime = true
        SDImageCache.shared().clearDisk(onCompletion: nil)
        return true
    }
    
    func application(_ application: UIApplication,
                     handleEventsForBackgroundURLSession identifier: String,
                     completionHandler: @escaping () -> Void) {
        
        AWSMobileClient.sharedInstance().initialize { (userState, error) in
            guard error == nil else {
                print("Error initializing AWSMobileClient. Error: \(error!.localizedDescription)")
                return
            }
            print("AWSMobileClient initialized.")
        }
        
        //provide the completionHandler to the TransferUtility to support background transfers.
        AWSS3TransferUtility.interceptApplication(application,
                                                  handleEventsForBackgroundURLSession: identifier,
                                                  completionHandler: completionHandler)
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        
    }

    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        var token = ""
        for i in 0..<deviceToken.count {
            token = token + String(format: "%02.2hhx", arguments: [deviceToken[i]])
        }
        if let navigationVC = self.window?.rootViewController as? UINavigationController {
            if let viewController = navigationVC.visibleViewController as? HomeVC {
                if let tokenSaved = UserDefaults.standard.string(forKey: "TokenKey") {
                    if tokenSaved == "" {
                        viewController.registerTokenForServer(token: token)
                        UserDefaults.standard.set(token, forKey: "TokenKey")
                    }
                } else {
                    viewController.registerTokenForServer(token: token)
                    UserDefaults.standard.set(token, forKey: "TokenKey")
                }
            }
        }
        
    }
    func checkUserState(completionHandler: @escaping (UserState)->()) {
        AWSMobileClient.sharedInstance().initialize { (userState, error) in
            if error == nil {
                if let userState = userState {
                    completionHandler(userState)
                }
            }
        }
    }
}
