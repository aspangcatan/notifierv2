package csmc.hospital.notifier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import csmc.hospital.notifier.data.remote.RetrofitClient
import csmc.hospital.notifier.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _changePasswordSuccess = MutableStateFlow(false)
    val changePasswordSuccess: StateFlow<Boolean> = _changePasswordSuccess.asStateFlow()

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        when {
            oldPassword.isBlank() -> { _error.value = "Old password is required."; return }
            oldPassword.length < 6 -> { _error.value = "Old password must be at least 6 characters."; return }
            newPassword.isBlank() -> { _error.value = "New password is required."; return }
            newPassword.length < 6 -> { _error.value = "New password must be at least 6 characters."; return }
            oldPassword == newPassword -> { _error.value = "New password must not be the same as old password."; return }
            newPassword != confirmPassword -> { _error.value = "Passwords do not match."; return }
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.changePassword(
                    id = UserSession.userId,
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
                if (response.code == 200) {
                    _changePasswordSuccess.value = true
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
    fun clearChangePasswordSuccess() { _changePasswordSuccess.value = false }
}
