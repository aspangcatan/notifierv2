package csmc.hospital.notifier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import csmc.hospital.notifier.data.session.UserSession
import csmc.hospital.notifier.ui.screens.*
import csmc.hospital.notifier.ui.viewmodel.LoginViewModel
import csmc.hospital.notifier.ui.viewmodel.ReferralViewModel
import csmc.hospital.notifier.ui.viewmodel.SettingsViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavigation(
    notificationDestination: String? = null,
    onNotificationConsumed: () -> Unit = {}
) {
    val navController = rememberNavController()
    val referralViewModel: ReferralViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    // Skip splash when launched from a notification (cold start)
    val startDest = notificationDestination ?: "splash"

    // Navigate when app was in the background and notification was tapped (onNewIntent)
    LaunchedEffect(notificationDestination) {
        notificationDestination?.let { dest ->
            if (navController.currentDestination?.route != dest) {
                navController.navigate(dest) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
            onNotificationConsumed()
        }
    }

    NavHost(navController = navController, startDestination = startDest) {
        composable("splash") {
            SplashScreen(onAnimationComplete = {
                val destination = if (UserSession.isLoggedIn) "triage_referral_list" else "login"
                navController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("triage_referral_list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("triage_referral_list") {
            TriageReferralListScreen(
                viewModel = referralViewModel,
                onViewDetails = { id, viewOnly, showHistory ->
                    navController.navigate("referral_details/$id?viewOnly=$viewOnly&showHistory=$showHistory")
                },
                onNavigateToQueue = {
                    navController.navigate("queue") {
                        popUpTo("triage_referral_list") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("queue") {
            QueueScreen(
                viewModel = referralViewModel,
                onNavigateToReferrals = {
                    navController.navigate("triage_referral_list") {
                        popUpTo("triage_referral_list") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "referral_details/{referralId}?viewOnly={viewOnly}&showHistory={showHistory}",
            arguments = listOf(
                navArgument("referralId") { type = NavType.StringType },
                navArgument("viewOnly") { type = NavType.BoolType; defaultValue = false },
                navArgument("showHistory") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val referralId = backStackEntry.arguments?.getString("referralId") ?: ""
            val viewOnly = backStackEntry.arguments?.getBoolean("viewOnly") ?: false
            val showHistory = backStackEntry.arguments?.getBoolean("showHistory") ?: false
            ReferralDetailsScreen(
                referralId = referralId,
                viewOnly = viewOnly,
                showHistory = showHistory,
                viewModel = referralViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("normal_user_referrals") {
            NormalUserReferralsScreen(
                viewModel = referralViewModel,
                onViewDetails = { id -> navController.navigate("referral_details/$id") }
            )
        }
    }
}
