package com.example.recipeapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import com.example.recipeapp.MainActivity
import com.example.recipeapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelID = "notification_channel"
const val channelName = "com.google.firebase.messaging.default_notification_channel_id"
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            generateNotification(it.title.orEmpty(), it.body.orEmpty())
        }
    }

    fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.recipeapp", R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.description, message)
        remoteView.setImageViewResource(R.id.logo, R.drawable.logo)

        return remoteView
    }

    fun generateNotification(title: String, message: String){

        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager  = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}