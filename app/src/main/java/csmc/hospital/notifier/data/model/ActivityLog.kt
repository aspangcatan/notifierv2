package csmc.hospital.notifier.data.model

data class ActivityLog(
    val origin: String = "",
    val destination: String = "",
    val remarks: String? = null,
    val date_tagged: String? = null,
    val date_forwarded: String? = null,
    val forwarded_by: Int? = null,
    val tagged_by: Int? = null,
    val forwarded_by_name: String? = null,
    val tagged_by_name: String? = null
)

data class ActivityLogResponse(
    val code: Int = 0,
    val results: List<ActivityLog> = emptyList(),
    val message: String = ""
)
