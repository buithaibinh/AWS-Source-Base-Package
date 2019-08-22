package com.skg.aws_sample

import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.skg.aws_sample.app.App
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var animationTop: Animation
    lateinit var animationBot: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //animation :))
        animationTop = AnimationUtils.loadAnimation(this, R.anim.animation_top)
        animationBot = AnimationUtils.loadAnimation(this, R.anim.animation_bottom)
        tvDemoAmazon.animation = animationTop
        imageAmazon.animation = animationBot

        Handler().postDelayed({
            App.instance.getPinpointManager(this,applicationContext)
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
        }, 2500)

    }

}
