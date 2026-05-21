package csmc.hospital.notifier.data.model

data class User(
    val id: Int = 0,
    val fname: String = "",
    val lname: String = "",
    val role: String = "",
    val topic: String = ""
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val code: Int = 0,
    val result: User? = null,
    val message: String = ""
)
