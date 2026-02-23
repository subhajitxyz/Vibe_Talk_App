package com.real.vibechat.data.firebasemessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.real.vibechat.MainActivity
import com.real.vibechat.R
import com.real.vibechat.utils.AppState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var firebaseAuth: FirebaseAuth
    @Inject lateinit var firestore: FirebaseFirestore

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateTokenInFirestore(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["senderName"] ?: "New Message"
        val body = remoteMessage.data["body"] ?: ""
        val chatRoomId = remoteMessage.data["chatRoomId"] ?: ""
        val senderId = remoteMessage.data["senderId"] ?: ""

        Log.d("TestNoti", "message recieved")

        // is user currently is in this chatroom. we will not show notification.
        if(AppState.activeChatRoomId == chatRoomId){
            return
        }
        showNotification(title, body, senderId)
    }

    private fun showNotification(title: String, message: String, userId: String) {
        Log.d("TestNoti", "in show noti")

        val channelId = "chat_channel"

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("vibechat://chat/$userId"),
            this,
            MainActivity::class.java
        )


        val pendingIntent = PendingIntent.getActivity(
            this,
            userId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.message_icon) // must exist
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun updateTokenInFirestore(token: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .update("fcmTokens", FieldValue.arrayUnion(token))
    }
}