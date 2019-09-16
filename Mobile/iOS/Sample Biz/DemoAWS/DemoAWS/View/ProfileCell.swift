//
//  ProfileCell.swift
//  DemoAWS
//
//  Created by Mai Dang Khoa on 8/8/19.
//  Copyright © 2019 SKG. All rights reserved.
//

import UIKit
import SDWebImage

protocol ProfileCellDelegate: class {
    func onChangeAvatar(_ sender: Any)
    func onHelloAll(_ sender: Any)
}

class ProfileCell: UITableViewCell {

    @IBOutlet weak var helloAllBtn: UIButton!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var avatarImageView: UIImageView!
    weak var delegate:ProfileCellDelegate? = nil
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        self.avatarImageView.image = UIImage.init(named: "avatar")
    }
    
    func setupCell(user: UserInfo) {
        self.emailLabel.text = user.email
        if let urlImage = URL.init(string: user.picture) {
            self.avatarImageView.sd_setImage(with: urlImage, completed: nil)
        }
    }
    
    func setupCell(user: AllUsersQuery.Data.AllUser.Item) {
        if let attributes = user.attributes {
            let userDetail = UserDetails.init(attributes: attributes)
            self.emailLabel.text = userDetail.email
            if let urlImage = URL.init(string: userDetail.picture) {
                self.avatarImageView.sd_setImage(with: urlImage, completed: nil)
            }
        }
    }
    
    @IBAction func changeAvatarBtnTapped(_ sender: Any) {
        if let delegate = self.delegate {
            delegate.onChangeAvatar(sender)
        }
    }
    @IBAction func helloAllTapped(_ sender: Any) {
        if let delegate = self.delegate {
            delegate.onHelloAll(sender)
        }
    }
}
