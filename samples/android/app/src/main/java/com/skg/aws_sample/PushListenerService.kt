package com.skg.aws_sample

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushListenerService : FirebaseMessagingService() {
    // Intent action used in local broadcast
    private val ACTION_PUSH_NOTIFICATION = "push-notification"
    // Intent keys
    private val INTENT_SNS_NOTIFICATION_FROM = "from"
    private val INTENT_SNS_NOTIFICATION_DATA = "data"
    private var mNotificationManager: NotificationManager? = null
    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        MainActivity.getPinpointManager(null,applicationContext).notificationClient.registerDeviceToken(token)
    }

    @SuppressLint("WrongConstant")
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val dataMap = HashMap(remoteMessage!!.data)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (mNotificationManager == null) {
            mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importence = NotificationManager.IMPORTANCE_HIGH
            var mChannel = mNotificationManager?.getNotificationChannel(getString(R.string.channel_id))
            if (mChannel == null) {
                mChannel = NotificationChannel(getString(R.string.channel_id), "chanel_demo", importence)
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                mNotificationManager?.createNotificationChannel(mChannel)
            }
            mBuilder.setContentTitle("hihi")
                .setSmallIcon(R.drawable.ic_send, 1)
                .setContentText(remoteMessage.data.toString())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setTicker("hahaha")
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))


        } else {
            mBuilder.setContentTitle("hihi")
                .setSmallIcon(R.drawable.ic_send, 1)
                .setContentText(remoteMessage.data.toString())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setTicker("hahaha")
                .setPriority(Notification.DEFAULT_SOUND)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        }
        mNotificationManager?.notify(0, mBuilder.build())

        broadcast(remoteMessage.from, dataMap)
    }

    private fun broadcast(from: String?, dataMap: HashMap<String, String>) {
        val intent = Intent(ACTION_PUSH_NOTIFICATION)
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from)
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, dataMap)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
