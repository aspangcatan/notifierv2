package csmc.hospital.notifier

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.messaging.FirebaseMessaging
import csmc.hospital.notifier.data.session.UserSession
import csmc.hospital.notifier.ui.navigation.AppNavigation
import csmc.hospital.notifier.ui.theme.NotifierTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    private var notificationNavDest by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSession.load(getSharedPreferences("user", Context.MODE_PRIVATE))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (UserSession.isLoggedIn && UserSession.topic.isNotBlank()) {
            FirebaseMessaging.getInstance()
                .subscribeToTopic("referrals_${UserSession.topic}")
        }

        if (intent.getBooleanExtra("from_notification", false)) {
            notificationNavDest = if (UserSession.isLoggedIn) "triage_referral_list" else "login"
        }

        enableEdgeToEdge()
        setContent {
            NotifierTheme {
                AppNavigation(
                    notificationDestination = notificationNavDest,
                    onNotificationConsumed = { notificationNavDest = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("from_notification", false)) {
            notificationNavDest = if (UserSession.isLoggedIn) "triage_referral_list" else "login"
        }
    }
}
