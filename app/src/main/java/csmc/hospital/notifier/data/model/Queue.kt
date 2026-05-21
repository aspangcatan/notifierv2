package csmc.hospital.notifier.data.model

data class QueueItem(
    val id: Int = 0,
    val referring_hospital: String = "",
    val patient: String = "",
    val age: Int = 0,
    val gender: String = "",
    val department: String = "",
    val remarks: String = "",
    val created_at: String = ""
)

data class QueueListResponse(
    val code: Int = 0,
    val results: List<QueueItem> = emptyList(),
    val message: String = ""
)
