package csmc.hospital.notifier.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashRed = Color(0xFFC62828)

@Composable
fun SplashScreen(onAnimationComplete: () -> Unit = {}) {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")

    val heartbeatScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1.0f at 0
                1.1f at 300
                1.0f at 600
                1.1f at 900
                1.0f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )

    val heartbeatAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.2f at 0
                0.4f at 300
                0.2f at 600
                0.4f at 900
                0.2f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    val loadingTransition = rememberInfiniteTransition(label = "loading")
    val loadingProgress by loadingTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "alpha"
    )
    val translateYAnim by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 10.dp,
        animationSpec = tween(durationMillis = 800, easing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)),
        label = "translateY"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashRed),
        contentAlignment = Alignment.Center
    ) {
        // Background heartbeat pattern
        Icon(
            imageVector = Icons.Default.MonitorHeart,
            contentDescription = null,
            modifier = Modifier
                .size(400.dp)
                .scale(heartbeatScale)
                .alpha(heartbeatAlpha),
            tint = Color.White
        )

        // Main Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .offset(y = translateYAnim)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize(),
                    tint = SplashRed
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Notifier",
                style = MaterialTheme.typography.displayLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
            )

            Text(
                text = "INTER-DEPARTMENTAL REFERRAL",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Loading bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .offset(x = 200.dp * loadingProgress)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Initializing secure systems...",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}
