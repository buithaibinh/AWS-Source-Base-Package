package com.skg.aws_sample.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService
import com.google.firebase.iid.FirebaseInstanceId
import com.skg.aws_sample.HomePage
import com.skg.aws_sample.LoginActivity
import com.skg.aws_sample.R


class App : Application() {
    lateinit var awsAppSyncClient : AWSAppSyncClient
    lateinit var pinpointManager: PinpointManager
    override fun onCreate() {
        super.onCreate()
        instance = this
        if (applicationContext.getSystemServiceName(App::class.java) != null) {
            try {
                applicationContext.startService(Intent(applicationContext, TransferService::class.java))
            } catch (e: Exception) {
                print(e.message)
            }
        }
        awsAppSyncClient = AWSAppSyncClient.builder()
            .context(applicationContext)
            .awsConfiguration(AWSConfiguration(applicationContext))
            .cognitoUserPoolsAuthProvider {
                try {
                    AWSMobileClient.getInstance().tokens.idToken.tokenString
                } catch (e: Exception) {
                    Log.e("APPSYNC_ERROR", e.localizedMessage)
                    e.localizedMessage
                }
            }
            .build()

    }

    companion object {
        lateinit var instance: App private set
    }
    fun getPinpointManager(activity: AppCompatActivity? = null, applicationContext: Context): PinpointManager {
        if (!::pinpointManager.isInitialized) {
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
                        Toast.makeText(activity?.applicationContext,"Init fails + ${e?.message}", Toast.LENGTH_LONG).show()
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
                    val sharedPref  = activity?.getSharedPreferences(activity.getString(R.string.token_devices),
                        Context.MODE_PRIVATE)
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