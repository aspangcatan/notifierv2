package csmc.hospital.notifier.data.session

import android.content.SharedPreferences

object UserSession {
    var userId: Int = 0
        private set
    var name: String = ""
        private set
    var role: String = ""
        private set
    var topic: String = ""
        private set

    val isLoggedIn: Boolean get() = userId > 0

    fun save(prefs: SharedPreferences, id: Int, fullName: String, role: String, topic: String) {
        userId = id
        name = fullName
        this.role = role
        this.topic = topic
        prefs.edit()
            .putInt("userid", id)
            .putString("name", fullName)
            .putString("role", role)
            .putString("topic", topic)
            .apply()
    }

    fun load(prefs: SharedPreferences) {
        userId = prefs.getInt("userid", 0)
        name = prefs.getString("name", "") ?: ""
        role = prefs.getString("role", "") ?: ""
        topic = prefs.getString("topic", "") ?: ""
    }

    fun clear(prefs: SharedPreferences) {
        userId = 0
        name = ""
        role = ""
        topic = ""
        prefs.edit().clear().apply()
    }
}
