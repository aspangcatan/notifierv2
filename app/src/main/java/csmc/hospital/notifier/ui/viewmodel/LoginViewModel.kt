package csmc.hospital.notifier.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import csmc.hospital.notifier.data.model.LoginRequest
import csmc.hospital.notifier.data.remote.RetrofitClient
import csmc.hospital.notifier.data.session.UserSession
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun login(username: String, password: String, prefs: SharedPreferences) {
        if (username.isBlank() || password.isBlank()) {
            _error.value = "Please enter your username and password."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(username, password))
                if (response.code == 200) {
                    val user = response.result
                    if (user != null) {
                        UserSession.save(
                            prefs = prefs,
                            id = user.id,
                            fullName = "${user.fname} ${user.lname}",
                            role = user.role,
                            topic = user.topic
                        )
                        if (user.topic.isNotBlank()) {
                            FirebaseMessaging.getInstance()
                                .subscribeToTopic("referrals_${user.topic}")
                        }
                        _loginSuccess.value = true
                    } else {
                        _error.value = "Unexpected server response. Please try again."
                    }
                } else {
                    _error.value = response.message
                }
            } catch (e: IOException) {
                _error.value = "Unable to connect. Please check your network connection."
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
