package com.skg.aws_sample.utils

import androidx.fragment.app.FragmentManager
import com.skg.aws_sample.dialog.DialogNotification

class Utils {
    companion object{
        fun showDialog(mess: String,supportFragmentManager : FragmentManager){
            val ft = supportFragmentManager.beginTransaction()
            val oldFrag = supportFragmentManager.findFragmentByTag("dialog_notification")
            if(oldFrag!= null){
                ft.remove(oldFrag)
            }
            ft.addToBackStack(null)
            val dialog= DialogNotification(mess)
            dialog.show(ft,"dialog_notification")
        }
    }
}