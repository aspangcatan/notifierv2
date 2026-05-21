package csmc.hospital.notifier.data.repository

import csmc.hospital.notifier.data.model.Department
import csmc.hospital.notifier.data.remote.RetrofitClient

class DepartmentRepository {
    private val api = RetrofitClient.apiService

    suspend fun getDepartments(): List<Department> {
        val response = api.getDepartments()
        return if (response.code == 200) response.results else emptyList()
    }
}
