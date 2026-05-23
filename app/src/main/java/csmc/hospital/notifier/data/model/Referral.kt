package csmc.hospital.notifier.data.model

data class Referral(
    val id: String,
    val patientCode: String,
    val hospital: String,
    val dateReferred: String = "",
    val rawDateReferred: String = "",
    val patientName: String,
    val patientAge: Int,
    val patientGender: String,
    val department: String,
    val priority: Priority,
    val timeLabel: String,
    val chiefComplaint: String,
    val diagnosis: String,
    val status: ReferralStatus = ReferralStatus.NEW,
    val type: String = "Referral"
)

enum class Priority {
    STAT, URGENT, HIGH, ROUTINE, PENDING
}

enum class ReferralStatus {
    NEW, PENDING_REVIEW, SCHEDULED, DONE
}
