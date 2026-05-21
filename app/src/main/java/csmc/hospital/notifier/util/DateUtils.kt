package csmc.hospital.notifier.util

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar(Char::uppercaseChar)
    }

fun String.toDisplayDate(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Manila")
        val date = sdf.parse(this) ?: return this
        SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH).format(date)
    } catch (e: Exception) {
        this
    }
}

fun String.toHumanDiff(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Manila")
        val date = sdf.parse(this) ?: return this
        val diff = System.currentTimeMillis() - date.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        when {
            seconds < 60 -> "just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days == 1L -> "yesterday"
            days < 7 -> "$days days ago"
            else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        this
    }
}
