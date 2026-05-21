package csmc.hospital.notifier.data.model

import com.google.firebase.database.IgnoreExtraProperties
import csmc.hospital.notifier.util.toDisplayDate
import csmc.hospital.notifier.util.toHumanDiff

@IgnoreExtraProperties
data class ReferralFirebase(
    val age: Int? = null,
    val chiefComplaint: String? = null,
    val date_referred: String? = null,
    val department: String? = null,
    val diagnosis: String? = null,
    val patient: String? = null,
    val patient_code: String? = null,
    val referring_hospital: String? = null,
    val sex: String? = null
) {
    fun toDomain(id: String): Referral {
        return Referral(
            id = id,
            patientCode = patient_code ?: "",
            hospital = referring_hospital ?: "Unknown Hospital",
            dateReferred = date_referred?.toDisplayDate() ?: "",
            rawDateReferred = date_referred ?: "",
            patientName = patient ?: "Unknown Patient",
            patientAge = age ?: 0,
            patientGender = sex ?: "Unknown",
            department = department ?: "General",
            priority = when {
                department?.contains("ER", ignoreCase = true) == true -> Priority.STAT
                else -> Priority.ROUTINE
            },
            timeLabel = date_referred?.toHumanDiff() ?: "",
            chiefComplaint = chiefComplaint ?: "",
            diagnosis = diagnosis ?: ""
        )
    }
}
