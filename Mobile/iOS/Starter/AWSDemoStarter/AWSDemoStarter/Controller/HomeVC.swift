//
//  ViewController.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/6/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import UserNotifications
import SDWebImage

class HomeVC: BaseVC {
    
    @IBOutlet weak var tableView: UITableView!
    
    var user:[String: Any] = [String: Any]()
    var userInfo:UserInfo? = nil
    let delegate = UIApplication.shared.delegate as! AppDelegate
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
    
    func userStateListener() {
        
    }
    
    //MARK: Load Avatar
    func loadAvatar() {
    }
    
    // MARK: Setup For Upload, Dowload
    func setupAWSUploadConfiguration() {
        
    }
    
    func downloadImage() {
        
    }
    
    @objc func upload(data: Data) {
        
    }
    
    // MARK: Setup Pinpoint
    func initPinPoint() {
        
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
    }
    
    func sendEventAttrMetric(eventName: String, attributeDict: [String: String], metricDict: [String: Double]) {
        
    }
    
    func sendMonetizationEvent(currency: String, itemPrice: Double, productId: String, quantity: Int) {
        
    }
    
    // MARK: Subscription
    func startUpdateUserSubscription() {
        
    }
    
    func fetchUsers() {
        
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
            complete(true)
        } else {
            complete(false)
        }
    }
    
    // MARK: Other
    @objc func onReport(_ sender: Any) {
        
    }
    
    @IBAction func logoutBtnTapped(_ sender: Any) {
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
