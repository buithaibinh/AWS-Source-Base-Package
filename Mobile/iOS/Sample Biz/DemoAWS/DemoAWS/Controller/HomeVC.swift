//
//  ViewController.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import AWSMobileClient
import UserNotifications
import AWSAppSync
import AWSPinpoint
import AWSS3
import SDWebImage

class HomeVC: BaseVC {
    
    @IBOutlet weak var tableView: UITableView!
    
    var user:[String: Any] = [String: Any]()
    var userInfo:UserInfo? = nil
    let delegate = UIApplication.shared.delegate as! AppDelegate
    var userList = [AllUsersQuery.Data.AllUser.Item]()
    var discard: Cancellable?
    var appSyncClient: AWSAppSyncClient? = nil
    var pinpoint: AWSPinpoint?
    @objc let imagePicker = UIImagePickerController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.imagePicker.delegate = self
        
        let button = UIButton(type: UIButton.ButtonType.custom)
        button.setTitle("Report", for: UIControl.State.normal)
        button.frame = CGRect.init(x: 0, y: 0, width: 30, height: 30)
        button.setTitleColor(UIColor.red, for: UIControl.State.normal)
        button.addTarget(self, action: #selector(onReport(_:)), for: UIControl.Event.touchUpInside)
        let barButton = UIBarButtonItem(customView: button)
        self.navigationItem.leftBarButtonItems = [barButton]
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.register(UINib.init(nibName: "ProfileCell", bundle: nil), forCellReuseIdentifier: "ProfileCell")
        
        self.navigationItem.hidesBackButton = true
        self.userInfo = UserInfo.init(dict: user)
        
        initPinPoint()
        userStateListener()
        registerForPushNotifications()
        loadAvatar()
        setupAWSUploadConfiguration()
        fetchUsers()
        startUpdateUserSubscription()
    }
    
    //MARK: Check user state
    func userStateListener() {
        AWSMobileClient.sharedInstance().addUserStateListener(self) { (userState, info) in
            switch (userState) {
            case .guest:
                DispatchQueue.main.async {
                    self.navigationController?.popToRootViewController(animated: true)
                }
            case .signedOut:
                DispatchQueue.main.async {
                    self.navigationController?.popToRootViewController(animated: true)
                }
            case .signedIn:
                break
            case .signedOutUserPoolsTokenInvalid:
                DispatchQueue.main.async {
                    self.navigationController?.popToRootViewController(animated: true)
                }
            case .signedOutFederatedTokensInvalid:
                DispatchQueue.main.async {
                    self.navigationController?.popToRootViewController(animated: true)
                }
            default:
                break
            }
        }
    }
    
    //MARK: Load Avatar
    func loadAvatar() {
        self.showIndicator()
        AWSMobileClient.sharedInstance().getUserAttributes { (dict, error) in
            if let dict = dict {
                let userDetail = UserDetails.init(dict: dict)
                if let urlImage = URL.init(string: userDetail.picture) {
                    DispatchQueue.main.async {
                        if let profileCell = self.tableView.cellForRow(at: IndexPath.init(row: 0, section: 0)) as? ProfileCell {
                            profileCell.avatarImageView.sd_setImage(with: urlImage, completed: nil)
                            self.hideIndicator()
                        }
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideIndicator()
                    }
                }
            }
        }
    }
    
    // MARK: Setup For Upload, Dowload
    func setupAWSUploadConfiguration() {
        let serviceConfiguration = AWSServiceConfiguration(region: .APSoutheast2, credentialsProvider: AWSMobileClient.sharedInstance().getCredentialsProvider())
        let transferUtilityConfigurationWithRetry = AWSS3TransferUtilityConfiguration()
        transferUtilityConfigurationWithRetry.isAccelerateModeEnabled = false
        transferUtilityConfigurationWithRetry.retryLimit = 10
        transferUtilityConfigurationWithRetry.multiPartConcurrencyLimit = 6
        transferUtilityConfigurationWithRetry.timeoutIntervalForResource = 15*60 //15 minutes
        
        if AWSS3TransferUtility.s3TransferUtility(forKey: "with-retry") != nil {
            return
        }
        AWSS3TransferUtility.register(
            with: serviceConfiguration!,
            transferUtilityConfiguration: transferUtilityConfigurationWithRetry,
            forKey: "with-retry"
        )
    }
    
    func downloadImage() {
        let expression = AWSS3TransferUtilityDownloadExpression()
        var completionHandler: AWSS3TransferUtilityDownloadCompletionHandlerBlock?
        completionHandler = { (task, URL, data, error) -> Void in
            DispatchQueue.main.async(execute: {
                if let data = data {
                    DispatchQueue.main.async {
                        if let profileCell = self.tableView.cellForRow(at: IndexPath.init(row: 0, section: 0)) as? ProfileCell {
                            profileCell.avatarImageView.image = UIImage.init(data: data)
                        }
                    }
                }
            })
        }
        if let identityId = AWSMobileClient.sharedInstance().getCredentialsProvider().identityId {
            if let completionHandler = completionHandler {
                let transferUtility = AWSS3TransferUtility.default()
                transferUtility.downloadData(
                    fromBucket: bucketName,
                    key: "protected/\(identityId)/test_avatar_0812_001.jpg",
                    expression: expression,
                    completionHandler: completionHandler
                    ).continueWith {
                        (task) -> AnyObject? in if let error = task.error {
                            print("Error: \(error.localizedDescription)")
                        }
                        
                        if let _ = task.result {
                            // Do something with downloadTask.
                        }
                        return nil;
                }
            }
        }
    }
    
    @objc func upload(data: Data) {
        self.showIndicator()
        let transferUtility = AWSS3TransferUtility.s3TransferUtility(forKey: "with-retry")
        let uploadExpression = AWSS3TransferUtilityUploadExpression()
        uploadExpression.setValue("public-read", forRequestHeader: "x-amz-acl")
        uploadExpression.progressBlock = {(task, progress) in
            if progress.fractionCompleted == 1.0 {
                DispatchQueue.main.async {
                    self.hideIndicator()
                }
            }
            print("Upload progress: ", progress.fractionCompleted)
        }
        if let identityId = AWSMobileClient.sharedInstance().getCredentialsProvider().identityId {
            let keyImage = "protected/\(identityId)/test_avatar_0812_001.jpg"
            let uploadCompletionHandler = { (task: AWSS3TransferUtilityUploadTask, error: Error?) -> Void in
                if let response = task.response {
                    if 200..<300 ~= response.statusCode {
                        print("Success?")
                        // Update User Attribute "Picture" when upload sucessfull
                        let url = "https://skg-dev-s3bucket-mbz2y336iyll.s3-ap-southeast-2.amazonaws.com/\(keyImage)"
                        AWSMobileClient.sharedInstance().updateUserAttributes(attributeMap: ["picture": url], completionHandler: { (userDetails, error) in
                            if let error = error {
                                print(error.localizedDescription)
                            }
                            if let _ = userDetails {
                                if let urlImage = URL.init(string: url) {
                                    DispatchQueue.main.async {
                                        if let profileCell = self.tableView.cellForRow(at: IndexPath.init(row: 0, section: 0)) as? ProfileCell {
                                            do {
                                                let data = try Data.init(contentsOf: urlImage)
                                                
                                                profileCell.avatarImageView.image = UIImage.init(data: data)
                                            } catch let e {
                                                print(e.localizedDescription)
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    } else {
                        DispatchQueue.main.async {
                            self.hideIndicator()
                        }
                        print("Failure?")
                        print(response.allHeaderFields.description)
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideIndicator()
                    }
                    print("Failure?")
                    print(error?.localizedDescription ?? "UNKNOW ERROR")
                }
            }
            
            let uploadTask = transferUtility?.uploadData(
                data,
                bucket: bucketName,
                key: keyImage,
                contentType: "image/jpg",
                expression: uploadExpression,
                completionHandler: uploadCompletionHandler
            )
            uploadTask?.continueWith (block: { (task) -> AnyObject? in
                print(task.error ?? "NOT ERROR")
                if let error = task.error {
                    DispatchQueue.main.async {
                        self.hideIndicator()
                    }
                    let alert = UIAlertController(title: "Error", message: error.localizedDescription, preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    self.present(alert, animated: true, completion: nil)
                }
                if let _ = task.result {
                }
                return nil
            })
        } else {
            self.hideIndicator()
        }
    }
    
    // MARK: Setup Pinpoint
    func initPinPoint() {
        let pinpointConfiguration = AWSPinpointConfiguration.defaultPinpointConfiguration(launchOptions: nil)
        pinpoint = AWSPinpoint(configuration: pinpointConfiguration)
    }
    
    func registerForPushNotifications() {
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) {
            (granted, error) in
            print("Permission granted: \(granted)")
            // 1. Check if permission granted
            guard granted else { return }
            // 2. Attempt registration for remote notifications on the main thread
            DispatchQueue.main.async {
                UIApplication.shared.registerForRemoteNotifications()
            }
        }
    }
    
    func registerTokenForServer(token: String) {
        if let user = self.userInfo {
            let url = URL(string: "https://ecoius1zkj.execute-api.ap-southeast-2.amazonaws.com/dev/register")!
            var request = URLRequest(url: url)
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.httpMethod = "POST"
            let parameters: [String: Any] = [
                "token": "\(token)",
                "platform": "APN",
                "userInfo": [
                    "id": user.sub,
                    "email": user.email
                ]
            ]
            let jsonData = try! JSONSerialization.data(withJSONObject: parameters, options: .prettyPrinted)
            request.httpBody = jsonData
            
            let task = URLSession.shared.dataTask(with: request) { data, response, error in
                if let error = error {
                    let alert = UIAlertController(title: "Error",
                                                  message: error.localizedDescription,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                } else {
                    if let data = data {
                        do {
                            let json = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments)
                            print(json as Any)
                        } catch let e {
                            print(e.localizedDescription)
                        }
                    }
                }
            }
            task.resume()
        }
    }
    
    func sayHello(to email: String, id: String) {
        self.showIndicator()
        let url = URL(string: "https://ecoius1zkj.execute-api.ap-southeast-2.amazonaws.com/dev/hello")!
        var request = URLRequest(url: url)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        let parameters: [String: Any] = [
            "message": "Hello",
            "userInfo": [
                "id": id,
                "email": email
            ]
        ]
        let jsonData = try! JSONSerialization.data(withJSONObject: parameters, options: .prettyPrinted)
        request.httpBody = jsonData
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                self.hideIndicator()
            }
            if let error = error {
                let alert = UIAlertController(title: "Error",
                                              message: error.localizedDescription,
                                              preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                DispatchQueue.main.async {
                    self.present(alert, animated: true, completion:nil)
                }
            } else {
                if let data = data {
                    do {
                        let json = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments)
                        print(json as Any)
                    } catch let e {
                        print(e.localizedDescription)
                    }
                }
            }
        }
        task.resume()
    }
    
    //MARK: Analytics
    func sendEvent(eventName: String, attributeName: String ,attributeValue: String, metricName: String, metricValue: Double) {
        if let analyticsClient = pinpoint?.analyticsClient {
            let event = analyticsClient.createEvent(withEventType: eventName)
            event.addAttribute(attributeValue, forKey: attributeName)
            event.addMetric(NSNumber(value: metricValue), forKey: metricName)
            analyticsClient.record(event)
            analyticsClient.submitEvents { (task) -> Any? in
                if let error = task.error {
                    let alert = UIAlertController(title: "Error",
                                                  message: error.localizedDescription,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                } else {
                    let alert = UIAlertController(title: "Success",
                                                  message: "Report Complete",
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                }
                return nil
            }
        }
    }
    
    func sendEventAttrMetric(eventName: String, attributeDict: [String: String], metricDict: [String: Double]) {
        if let analyticsClient = pinpoint?.analyticsClient {
            let event = analyticsClient.createEvent(withEventType: eventName)
            for (key, value) in attributeDict {
                event.addAttribute(value, forKey: key)
            }
            for (key, value) in metricDict {
                event.addMetric(NSNumber(value: value), forKey: key)
            }
            analyticsClient.record(event)
            analyticsClient.submitEvents { (task) -> Any? in
                if let error = task.error {
                    let alert = UIAlertController(title: "Error",
                                                  message: error.localizedDescription,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                } else {
                    let alert = UIAlertController(title: "Success",
                                                  message: "Report Complete",
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                }
                return nil
            }
        }
    }
    
    func sendMonetizationEvent(currency: String, itemPrice: Double, productId: String, quantity: Int) {
        if let analyticsClient = pinpoint?.analyticsClient {
            let event = analyticsClient.createVirtualMonetizationEvent(
                withProductId: productId,
                withItemPrice: itemPrice,
                withQuantity: quantity,
                withCurrency: currency
            )
            analyticsClient.record(event)
            analyticsClient.submitEvents { (task) -> Any? in
                if let error = task.error {
                    let alert = UIAlertController(title: "Error",
                                                  message: error.localizedDescription,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                } else {
                    let alert = UIAlertController(title: "Success",
                                                  message: "Report Complete",
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                }
                return nil
            }
        }
    }
    
    // MARK: Subscription
    func startUpdateUserSubscription() {
        do {
            self.discard = try self.appSyncClient?.subscribe(subscription: OnUpdateUserSubscription.init(), resultHandler: { (result, transitions, error) in
                if let error = error {
                    let alert = UIAlertController(title: "Error",
                                                  message: error.localizedDescription,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                } else if let result = result {
                    // Check case delete
                    if result.data?.onUpdateUser?.userCreateDate == nil {
                        let username = result.data?.onUpdateUser?.username
                        self.userList.removeAll(where: { $0.username == username })
                    } else {
                        // add new
                        if let createdUser = result.data?.onUpdateUser {
                            let attributes = createdUser.attributes?.map({ (att) -> AllUsersQuery.Data.AllUser.Item.Attribute in
                                return AllUsersQuery.Data.AllUser.Item.Attribute.init(name: att.name, value: att.value)
                            })
                            let newUser = AllUsersQuery.Data.AllUser.Item.init(
                                username: createdUser.username,
                                attributes: attributes,
                                userCreateDate: createdUser.userCreateDate,
                                userLastModifiedDate: createdUser.userLastModifiedDate,
                                enabled: createdUser.enabled,
                                userStatus: createdUser.userStatus,
                                groups: nil)
                            self.userList.append(newUser)
                        }
                    }
                    
                    DispatchQueue.main.async {
                        self.tableView.reloadData()
                    }
                }
            })
        } catch let error {
            print(error.localizedDescription)
        }
    }
    
    // MARK: Appsync AllUsersQuery
    /**
        In case of using API public, init like below
        let appSyncServiceConfig = try AWSAppSyncServiceConfig.init(forKey: "other key")
        let appSyncConfig = try AWSAppSyncClientConfiguration(appSyncServiceConfig: appSyncServiceConfig,
                                                    cacheConfiguration: cacheConfiguration)
     
        In case of using API private, init like below
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
        let appSyncServiceConfig = try AWSAppSyncServiceConfig.init(forKey: "Default")
        let appSyncConfig = try AWSAppSyncClientConfiguration(
                                appSyncServiceConfig: appSyncServiceConfig,
                                userPoolsAuthProvider: {
                                    return MyCognitoUserPoolsAuthProvider()
                                }(),cacheConfiguration: cacheConfiguration)
    **/
    
    func fetchUsers() {
        do {
            let cacheConfiguration = try AWSAppSyncCacheConfiguration()
            
            let appSyncPrivateConfig = try AWSAppSyncClientConfiguration(
                appSyncServiceConfig: AWSAppSyncServiceConfig(),
                userPoolsAuthProvider: {
                    return MyCognitoUserPoolsAuthProvider()
            }(),cacheConfiguration: cacheConfiguration)
            
            self.appSyncClient = try AWSAppSyncClient(appSyncConfig: appSyncPrivateConfig)
            
            self.showIndicator()
            self.appSyncClient?.fetch(query: AllUsersQuery.init(), cachePolicy: .fetchIgnoringCacheData, queue: DispatchQueue.main, resultHandler: { (task, error) in
                self.hideIndicator()
                if let error = error {
                    let alert = UIAlertController(title: "Error",
                                                  message: error.localizedDescription,
                                                  preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    DispatchQueue.main.async {
                        self.present(alert, animated: true, completion:nil)
                    }
                } else if let task = task {
                    if let items = task.data?.allUsers.items {
                        self.userList = items.filter({ (item) -> Bool in
                            return item.username != self.userInfo?.username
                        })
                        DispatchQueue.main.async {
                            self.tableView.reloadData()
                        }
                    }
                }
            })
        } catch {
            print("Error initializing appsync client. \(error)")
        }
    }
    
    // Check admin
    internal func isAdmin() -> Bool {
        if let user = self.userInfo {
            let isAdmin: Bool = user.groups.map({ $0.lowercased() }).contains("admin")
            return isAdmin
        }
        
        return false
    }
    internal func deleteIfAdmin(username: String, complete:@escaping (Bool) -> Void) {
        let isAdmin = self.isAdmin()
        // Admin function
        if (isAdmin) {
            self.appSyncClient?.perform(mutation: DeleteUserMutation(userName: username), queue: DispatchQueue.main, optimisticUpdate: nil, conflictResolutionBlock: nil, resultHandler: { (result, error) in
                if let err = error {
                    // Show error
                    print(err.localizedDescription)
                    complete(false)
                    let alert = UIAlertController(title: "Error", message: err.localizedDescription, preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                    self.present(alert, animated: true, completion: nil)
                } else {
                    // Success
                    complete(true)
                }
            })
        } else {
            complete(false)
            let alert = UIAlertController(title: "Error", message: "Bạn không phải ADMIN", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    // MARK: Other
    @objc func onReport(_ sender: Any) {
        self.sendEvent(eventName: "Test Event", attributeName: "DemoAttribute1", attributeValue: "DemoAttributeValue1", metricName: "metric1", metricValue: 100)
        
        //Custom Event
//        self.sendEventAttrMetric(eventName: "Test ", attributeDict: ["DemoAttributeValue1": "DemoAttribute1", "DemoAttributeValue2": "DemoAttribute2"], metricDict: ["EventName" : Double(arc4random() % 65535)])
        
        //Monetization Event
//        self.sendMonetizationEvent(currency: "DEMO_PRODUCT_ID", itemPrice: 1.0, productId: "USD", quantity: 1)
    }
    
    @IBAction func logoutBtnTapped(_ sender: Any) {
        AWSMobileClient.sharedInstance().signOut()
        self.navigationController?.popViewController(animated: true)
    }
}

extension HomeVC: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1;
        } else {
            return self.userList.count
        }
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.section {
        case 0:
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProfileCell", for: indexPath) as! ProfileCell
            cell.delegate = self
            cell.setupCell(user: self.userInfo!)
            cell.helloAllBtn.isHidden = false
            return cell
        default:
            let cell = tableView.dequeueReusableCell(withIdentifier: "ProfileCell", for: indexPath) as! ProfileCell
            let user:AllUsersQuery.Data.AllUser.Item = self.userList[indexPath.row]
            cell.setupCell(user: user)
            cell.helloAllBtn.isHidden = true
            return cell
        }
    }
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if indexPath.section == 0 {
            return 113
        }
        return 50
    }
    
    @available(iOS 11.0, *)
    func tableView(_ tableView: UITableView, trailingSwipeActionsConfigurationForRowAt indexPath: IndexPath) -> UISwipeActionsConfiguration? {
        if indexPath.section == 1 {
            let editAction = UIContextualAction(style: .normal, title:  "Say Hello", handler: { [weak self] (ac:UIContextualAction, view:UIView, success:(Bool) -> Void) in
                guard let `self` = self else { return }
                let userDetail = UserDetails.init(attributes: self.userList[indexPath.row].attributes ?? [])
                self.sayHello(to: userDetail.email, id: userDetail.sub)
                success(true)
            })
            editAction.backgroundColor = .blue
            
            var actions = [editAction]
            
            if self.isAdmin() {
                let deleteAction = UIContextualAction(style: .destructive, title:  "Delete", handler: { (ac:UIContextualAction, view:UIView, success:@escaping (Bool) -> Void) in
                    // TODO: Delete user if Admin
                    self.deleteIfAdmin(username: self.userList[indexPath.row].username, complete: { (isSuccess) in
                        success(isSuccess)
                        if isSuccess {
                            self.userList.remove(at: indexPath.row)
                            tableView.reloadData()
                        }
                    })
                })
                actions.append(deleteAction)
            }
            let configuration = UISwipeActionsConfiguration(actions: actions)
            configuration.performsFirstActionWithFullSwipe = false
            return configuration
        }
        return nil
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        self.discard?.cancel()
        SDImageCache.shared().clearMemory()
        SDImageCache.shared().clearDisk(onCompletion: nil)
    }
    
}

extension HomeVC: UNUserNotificationCenterDelegate {
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        print("User Info = ",notification.request.content.userInfo)
        let alert = UIAlertController(title: "Notification Received",
                                      message: notification.request.content.userInfo.description,
                                      preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
        
        self.present(alert, animated: true, completion:nil)
        completionHandler([.sound])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        print("User Info = ",response.notification.request.content.userInfo)
        completionHandler()
    }
}

extension HomeVC: ProfileCellDelegate {
    func onChangeAvatar(_ sender: Any) {
        imagePicker.allowsEditing = false
        imagePicker.sourceType = .photoLibrary
        
        present(imagePicker, animated: true, completion: nil)
    }
    func onHelloAll(_ sender: Any) {
        self.showIndicator()
        let url = URL(string: "https://ecoius1zkj.execute-api.ap-southeast-2.amazonaws.com/dev/hello")!
        var request = URLRequest(url: url)
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        let parameters: [String: Any] = [
            "message": "Hello"
        ]
        let jsonData = try! JSONSerialization.data(withJSONObject: parameters, options: .prettyPrinted)
        request.httpBody = jsonData
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                self.hideIndicator()
            }
            if let error = error {
                let alert = UIAlertController(title: "Error",
                                              message: error.localizedDescription,
                                              preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: nil))
                DispatchQueue.main.async {
                    self.present(alert, animated: true, completion:nil)
                }
            } else {
                if let data = data {
                    do {
                        let json = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments)
                        print(json as Any)
                    } catch let e {
                        print(e.localizedDescription)
                    }
                }
            }
        }
        task.resume()
    }
}

extension HomeVC: UIImagePickerControllerDelegate {
    
    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        let info = convertFromUIImagePickerControllerInfoKeyDictionary(info)
        
        if "public.image" == info[convertFromUIImagePickerControllerInfoKey(UIImagePickerController.InfoKey.mediaType)] as? String {
            let image: UIImage = info[convertFromUIImagePickerControllerInfoKey(UIImagePickerController.InfoKey.originalImage)] as! UIImage
            self.upload(data: image.jpegData(compressionQuality: 0.5)!)
        }
        
        dismiss(animated: true, completion: nil)
    }
}

fileprivate func convertFromUIImagePickerControllerInfoKeyDictionary(_ input: [UIImagePickerController.InfoKey: Any]) -> [String: Any] {
    return Dictionary(uniqueKeysWithValues: input.map {key, value in (key.rawValue, value)})
}

fileprivate func convertFromUIImagePickerControllerInfoKey(_ input: UIImagePickerController.InfoKey) -> String {
    return input.rawValue
}

extension HomeVC: UINavigationControllerDelegate {
    
}
