package com.skg.aws_sample.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.amazonaws.mobile.client.*
import com.amazonaws.mobile.client.results.Tokens
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService
import com.skg.aws_sample.HomePage
import com.skg.aws_sample.LoginActivity
import com.skg.aws_sample.MainActivity


class App : Application() {
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
        val appContext: Context get() = instance.applicationContext
        val baseContext: Context get() = instance.baseContext
        lateinit var awsAppSyncClient : AWSAppSyncClient
        lateinit var pinpointManager: PinpointManager
        fun pinpointManager(pinpointManager: PinpointManager){
           this.pinpointManager = pinpointManager
        }
    }

}