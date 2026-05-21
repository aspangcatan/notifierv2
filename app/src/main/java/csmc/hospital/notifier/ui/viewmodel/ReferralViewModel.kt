package csmc.hospital.notifier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import csmc.hospital.notifier.data.model.ActivityLog
import csmc.hospital.notifier.data.model.Department
import csmc.hospital.notifier.data.model.QueueItem
import csmc.hospital.notifier.data.model.Referral
import csmc.hospital.notifier.data.model.ReferralApiItem
import csmc.hospital.notifier.data.remote.RetrofitClient
import csmc.hospital.notifier.data.session.UserSession
import csmc.hospital.notifier.data.repository.DepartmentRepository
import csmc.hospital.notifier.data.repository.FirebaseReferralRepository
import csmc.hospital.notifier.data.repository.ReferralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReferralViewModel(
    private val repository: ReferralRepository = FirebaseReferralRepository(),
    private val departmentRepository: DepartmentRepository = DepartmentRepository()
) : ViewModel() {

    private val _referrals = MutableStateFlow<List<Referral>>(emptyList())
    val referrals: StateFlow<List<Referral>> = _referrals.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _isDepartmentsLoading = MutableStateFlow(false)
    val isDepartmentsLoading: StateFlow<Boolean> = _isDepartmentsLoading.asStateFlow()

    private val _isForwarding = MutableStateFlow(false)
    val isForwarding: StateFlow<Boolean> = _isForwarding.asStateFlow()

    private val _forwardSuccess = MutableStateFlow(false)
    val forwardSuccess: StateFlow<Boolean> = _forwardSuccess.asStateFlow()

    private val _tagDoneSuccess = MutableStateFlow(false)
    val tagDoneSuccess: StateFlow<Boolean> = _tagDoneSuccess.asStateFlow()

    private val _isTagDoneLoading = MutableStateFlow(false)
    val isTagDoneLoading: StateFlow<Boolean> = _isTagDoneLoading.asStateFlow()

    private val _queueItems = MutableStateFlow<List<QueueItem>>(emptyList())
    val queueItems: StateFlow<List<QueueItem>> = _queueItems.asStateFlow()

    private val _isQueueLoading = MutableStateFlow(false)
    val isQueueLoading: StateFlow<Boolean> = _isQueueLoading.asStateFlow()

    private val _isQueuing = MutableStateFlow(false)
    val isQueuing: StateFlow<Boolean> = _isQueuing.asStateFlow()

    private val _queueSuccess = MutableStateFlow(false)
    val queueSuccess: StateFlow<Boolean> = _queueSuccess.asStateFlow()

    private val _deptReferrals = MutableStateFlow<List<Referral>>(emptyList())
    val deptReferrals: StateFlow<List<Referral>> = _deptReferrals.asStateFlow()

    private val _isDeptLoading = MutableStateFlow(false)
    val isDeptLoading: StateFlow<Boolean> = _isDeptLoading.asStateFlow()

    private val _allDeptReferrals = MutableStateFlow<List<Referral>>(emptyList())
    val allDeptReferrals: StateFlow<List<Referral>> = _allDeptReferrals.asStateFlow()

    private val _isAllDeptLoading = MutableStateFlow(false)
    val isAllDeptLoading: StateFlow<Boolean> = _isAllDeptLoading.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _isLogsLoading = MutableStateFlow(false)
    val isLogsLoading: StateFlow<Boolean> = _isLogsLoading.asStateFlow()

    private val _logsError = MutableStateFlow<String?>(null)
    val logsError: StateFlow<String?> = _logsError.asStateFlow()

    init {
        loadReferrals()
    }

    fun getReferralById(id: String): Referral? = _referrals.value.find { it.id == id }

    fun loadDepartments() {
        if (_departments.value.isNotEmpty()) return
        viewModelScope.launch {
            _isDepartmentsLoading.value = true
            try {
                _departments.value = departmentRepository.getDepartments()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isDepartmentsLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearForwardSuccess() {
        _forwardSuccess.value = false
    }

    fun clearTagDoneSuccess() {
        _tagDoneSuccess.value = false
    }

    fun clearQueueSuccess() {
        _queueSuccess.value = false
    }

    fun loadActivityLogs(referralId: String) {
        viewModelScope.launch {
            _isLogsLoading.value = true
            _activityLogs.value = emptyList()
            try {
                val response = RetrofitClient.apiService.getActivityLogs(referralId)
                if (response.code == 200) {
                    _activityLogs.value = response.results
                } else {
                    _logsError.value = response.message.ifBlank { "Failed to load history." }
                }
            } catch (e: Exception) {
                _logsError.value = e.message ?: "Failed to load history."
            } finally {
                _isLogsLoading.value = false
            }
        }
    }

    fun clearLogsError() { _logsError.value = null }

    fun loadQueue() {
        viewModelScope.launch {
            _isQueueLoading.value = true
            try {
                val response = RetrofitClient.apiService.getQueueList()
                if (response.code == 200) {
                    _queueItems.value = response.results
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isQueueLoading.value = false
            }
        }
    }

    fun queueReferral(referralId: String, department: Department, userId: Int = UserSession.userId) {
        val referral = _referrals.value.find { it.id == referralId } ?: return
        viewModelScope.launch {
            _isQueuing.value = true
            try {
                val response = RetrofitClient.apiService.queueReferral(
                    referringHospital = referral.hospital,
                    patient = referral.patientName,
                    age = referral.patientAge,
                    gender = referral.patientGender,
                    department = department.code,
                    userid = userId,
                    remarks = "QUEUED FROM TRIAGE"
                )
                if (response.code == 200) {
                    _queueSuccess.value = true
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isQueuing.value = false
            }
        }
    }

    fun tagAsDone(referralId: String, userId: Int = UserSession.userId) {
        val referral = _referrals.value.find { it.id == referralId } ?: return
        viewModelScope.launch {
            _isTagDoneLoading.value = true
            try {
                val insertResponse = RetrofitClient.apiService.insertReferral(
                    userid = userId,
                    firebaseKey = referralId,
                    patient = referral.patientName,
                    gender = referral.patientGender,
                    age = referral.patientAge,
                    diagnosis = referral.diagnosis,
                    chiefComplaint = referral.chiefComplaint,
                    department = referral.department,
                    code = UserSession.topic,
                    referringHospital = referral.hospital,
                    remarks = "DONE",
                    dateCreated = referral.rawDateReferred
                )
                if (insertResponse.code == 200 || insertResponse.message == "Referral already added") {
                    val doneResponse = RetrofitClient.apiService.tagAsDone(
                        firebaseKey = referralId,
                        userid = userId,
                        remarks = "DONE"
                    )
                    if (doneResponse.code == 200) {
                        repository.deleteReferral(referralId)
                        _tagDoneSuccess.value = true
                    } else {
                        _error.value = doneResponse.message
                    }
                } else {
                    _error.value = insertResponse.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isTagDoneLoading.value = false
            }
        }
    }

    fun forwardReferral(referralId: String, department: Department, remarks: String, userId: Int = UserSession.userId) {
        val referral = _referrals.value.find { it.id == referralId } ?: return
        viewModelScope.launch {
            _isForwarding.value = true
            try {
                val response = RetrofitClient.apiService.insertReferral(
                    userid = userId,
                    firebaseKey = referralId,
                    patient = referral.patientName,
                    gender = referral.patientGender,
                    age = referral.patientAge,
                    diagnosis = referral.diagnosis,
                    chiefComplaint = referral.chiefComplaint,
                    department = department.code,
                    code = department.code,
                    referringHospital = referral.hospital,
                    remarks = remarks,
                    dateCreated = referral.rawDateReferred
                )
                if (response.code == 200 || response.message == "Referral already added") {
                    repository.deleteReferral(referralId)
                    _forwardSuccess.value = true
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isForwarding.value = false
            }
        }
    }

    fun loadDeptReferrals(department: String = UserSession.topic) {
        viewModelScope.launch {
            _isDeptLoading.value = true
            try {
                val response = RetrofitClient.apiService.getReferrals(department = department)
                if (response.code == 200) {
                    _deptReferrals.value = response.results
                        .map { it.toDomain() }
                        .sortedByDescending { it.rawDateReferred }
                } else {
                    _error.value = response.message.ifBlank { "Failed to load referrals." }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isDeptLoading.value = false
            }
        }
    }

    fun loadAllDeptReferrals() {
        viewModelScope.launch {
            _isAllDeptLoading.value = true
            try {
                val response = RetrofitClient.apiService.getAllReferrals()
                if (response.code == 200) {
                    _allDeptReferrals.value = response.results
                        .map { it.toDomain() }
                        .sortedByDescending { it.rawDateReferred }
                } else {
                    _error.value = response.message.ifBlank { "Failed to load referrals." }
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isAllDeptLoading.value = false
            }
        }
    }

    private fun loadReferrals() {
        viewModelScope.launch {
            repository.getReferrals()
                .catch { e -> _error.value = e.message }
                .collect { list ->
                    _referrals.value = list.sortedByDescending { it.rawDateReferred }
                    _isLoading.value = false
                }
        }
    }
}
