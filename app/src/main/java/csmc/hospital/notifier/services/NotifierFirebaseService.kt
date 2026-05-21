package csmc.hospital.notifier.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import csmc.hospital.notifier.MainActivity
import csmc.hospital.notifier.R
import csmc.hospital.notifier.data.session.UserSession

class NotifierFirebaseService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "notifier_referrals"
        const val CHANNEL_NAME = "Referral Notifications"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data

        val hospital = data["hospital_referrer"] ?: data["referring_hospital"] ?: "Unknown Hospital"
        val patient = data["patient"] ?: "Unknown Patient"
        val age = data["age"] ?: ""
        val sex = data["sex"] ?: data["gender"] ?: ""

        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("New referral from: $hospital")
            .setContentText("Patient: $patient ($age/$sex)")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOnlyAlertOnce(false)
            .build()

        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            NotificationManagerCompat.from(this)
                .notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    override fun onNewToken(token: String) {
        val prefs = getSharedPreferences("user", android.content.Context.MODE_PRIVATE)
        UserSession.load(prefs)
        if (UserSession.isLoggedIn && UserSession.topic.isNotBlank()) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance()
                .subscribeToTopic("referrals_${UserSession.topic}")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming patient referrals"
                enableLights(true)
                enableVibration(true)
            }
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }
}
