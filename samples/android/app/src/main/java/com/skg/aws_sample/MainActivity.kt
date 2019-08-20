package com.skg.aws_sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.skg.aws_sample.app.App
import com.skg.aws_sample.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var animationTop: Animation
    lateinit var animationBot: Animation
    private lateinit var pinpointManager: PinpointManager
    companion object{
        fun getPinpointManager(activity: AppCompatActivity? = null, applicationContext: Context): PinpointManager {
            var pinpointManager: PinpointManager?= null
            if (pinpointManager == null) {
                val awsConfig = AWSConfiguration(applicationContext)
                AWSMobileClient.getInstance()
                    .initialize(applicationContext, awsConfig, object : Callback<UserStateDetails> {
                        override fun onResult(result: UserStateDetails?) {
                            when(result?.userState){
                                UserState.SIGNED_IN -> {
                                    activity?.runOnUiThread {
                                        activity.startActivity(Intent(activity, HomePage::class.java))
                                    }
                                }
                                UserState.SIGNED_OUT -> {
                                    activity?.runOnUiThread {
                                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                                    }
                                }
                                UserState.SIGNED_OUT_USER_POOLS_TOKENS_INVALID -> {
                                    activity?.runOnUiThread {
                                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                                    }
                                }
                                UserState.SIGNED_OUT_FEDERATED_TOKENS_INVALID -> {
                                    activity?.runOnUiThread {
                                        activity.startActivity(Intent(activity, LoginActivity::class.java))
                                    }
                                }
                                else -> activity?.runOnUiThread {
                                    activity.startActivity(Intent(activity, LoginActivity::class.java))
                                }
                            }
                        }

                        override fun onError(e: java.lang.Exception?) {
                            Toast.makeText(activity?.applicationContext,"Init fails + ${e?.message}",Toast.LENGTH_LONG).show()
                        }
                    })

                val pinpointConfig = PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig
                )

                pinpointManager = PinpointManager(pinpointConfig)

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener { task ->
                        val token = task.result!!.token
                        val sharedPref  = activity?.getSharedPreferences(activity.getString(R.string.token_devices),Context.MODE_PRIVATE)
                        val oldToken = sharedPref?.getString(activity.getString(R.string.save_token),"")
                        if(oldToken.isNullOrEmpty()|| oldToken != token){
                            with(sharedPref?.edit()){
                                this?.putString(activity?.getString(R.string.save_token),token)
                                this?.apply()
                            }
                        }else{}

                        pinpointManager.notificationClient.registerDeviceToken(token)
                    }
            }
            return pinpointManager
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //animation :))
        animationTop = AnimationUtils.loadAnimation(this, R.anim.animation_top)
        animationBot = AnimationUtils.loadAnimation(this, R.anim.animation_bottom)
        tvDemoAmazon.animation = animationTop
        imageAmazon.animation = animationBot
        Handler().postDelayed({
            pinpointManager = getPinpointManager(this,applicationContext)
            App.pinpointManager(pinpointManager)
            pinpointManager.sessionClient.startSession()
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            logEvent()
        }, 2500)

    }

    private fun logEvent() {
        val event = pinpointManager.analyticsClient.createEvent("EventName")
            .withAttribute("StartApp", "ByNamAnh")
            .withMetric("DemoMetric1", Math.random())
        pinpointManager.analyticsClient.recordEvent(event)
    }
    override fun onDestroy() {
        super.onDestroy()
        pinpointManager.sessionClient.stopSession()
        pinpointManager.analyticsClient.submitEvents()
    }
}
