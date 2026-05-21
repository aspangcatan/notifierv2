package csmc.hospital.notifier.data.model

data class Department(
    val department: String = "",
    val code: String = ""
)

data class DepartmentResponse(
    val code: Int = 0,
    val results: List<Department> = emptyList(),
    val message: String = ""
)

data class ForwardReferralResponse(
    val code: Int = 0,
    val message: String = ""
)
