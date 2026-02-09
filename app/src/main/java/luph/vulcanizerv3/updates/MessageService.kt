package luph.vulcanizerv3.updates

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessageService : FirebaseMessagingService() {
    private val TAG = "MessageService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Token")
    }


    private fun sendNotification(notification: RemoteMessage.Notification, openLink: String? = null) {
        val intent = if (openLink != null) {
            Intent(Intent.ACTION_VIEW, Uri.parse(openLink))
        } else {
            Intent(this, MainActivity::class.java) // Fallback to MainActivity if no link is provided
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, notification.channelId ?: "default")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(notification.channelId ?: "default", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)


        notificationManager.notify(openLink?.hashCode() ?: System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        var openLink: String? = null

        if (remoteMessage.data.isNotEmpty()) {
            remoteMessage.data.get("open")?.let {
                openLink = "https://vulcanupdates.web.app/${it.replace(" ", "%20")}"
            }
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it, openLink)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}