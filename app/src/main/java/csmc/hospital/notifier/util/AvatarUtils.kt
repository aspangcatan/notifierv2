package csmc.hospital.notifier.util

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import csmc.hospital.notifier.R

object AvatarUtils {
    fun getAvatarResource(age: Int, gender: String): Int {
        val isMale = !gender.contains("female", ignoreCase = true)
        return when {
            age <= 12 -> if (isMale) R.drawable.avatar_0_12_male else R.drawable.avatar_0_12_female
            age <= 59 -> if (isMale) R.drawable.avatar_13_59_male else R.drawable.avatar_13_59_female
            else -> if (isMale) R.drawable.avatar_60_plus_male else R.drawable.avatar_60_plus_female
        }
    }

    @Composable
    fun PatientAvatar(age: Int, gender: String, modifier: Modifier = Modifier) {
        Image(
            painter = painterResource(id = getAvatarResource(age, gender)),
            contentDescription = "Patient Avatar",
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
    }
}
