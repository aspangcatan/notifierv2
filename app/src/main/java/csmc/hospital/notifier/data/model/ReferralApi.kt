package csmc.hospital.notifier.data.model

import csmc.hospital.notifier.util.toDisplayDate
import csmc.hospital.notifier.util.toHumanDiff

data class ReferralApiItem(
    val firebase_key: String = "",
    val patient: String = "",
    val gender: String = "",
    val age: Int = 0,
    val diagnosis: String = "",
    val chief_complaint: String = "",
    val department: String = "",
    val referring_hospital: String = "",
    val date_created: String = "",
    val type: String = "Referral"
) {
    fun toDomain() = Referral(
        id = firebase_key,
        patientCode = firebase_key,
        hospital = referring_hospital,
        dateReferred = date_created.toDisplayDate(),
        rawDateReferred = date_created,
        patientName = patient,
        patientAge = age,
        patientGender = gender,
        department = department,
        priority = Priority.ROUTINE,
        timeLabel = date_created.toHumanDiff(),
        chiefComplaint = chief_complaint,
        diagnosis = diagnosis,
        type = type.ifBlank { "Referral" }
    )
}

data class ReferralListResponse(
    val code: Int = 0,
    val results: List<ReferralApiItem> = emptyList(),
    val message: String = ""
)
