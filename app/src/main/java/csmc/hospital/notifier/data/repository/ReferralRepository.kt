package csmc.hospital.notifier.data.repository

import csmc.hospital.notifier.data.model.Priority
import csmc.hospital.notifier.data.model.Referral
import csmc.hospital.notifier.data.model.ReferralStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ReferralRepository {
    fun getReferrals(): Flow<List<Referral>>
    fun getReferralById(id: String): Referral?
    suspend fun deleteReferral(id: String)
}

class MockReferralRepository : ReferralRepository {
    private val mockData = listOf(
        Referral(
            id = "#PX-9921",
            patientCode = "#PX-9921",
            hospital = "St. Mary's General",
            patientName = "Jonathan Miller",
            patientAge = 45,
            patientGender = "Male",
            department = "Cardiology",
            priority = Priority.URGENT,
            timeLabel = "2 hours ago",
            chiefComplaint = "Acute chest pain radiating to the left arm.",
            diagnosis = "STEMI suspected based on EKG leads V2-V4."
        ),
        Referral(
            id = "#PX-0412",
            patientCode = "#PX-0412",
            hospital = "City Trauma Center",
            patientName = "Elena Rodriguez",
            patientAge = 29,
            patientGender = "Female",
            department = "Emergency",
            priority = Priority.STAT,
            timeLabel = "15 mins ago",
            chiefComplaint = "Severe trauma following a motor vehicle accident.",
            diagnosis = "Multiple rib fractures, potential internal bleeding."
        ),
        Referral(
            id = "#PX-8830",
            patientCode = "#PX-8830",
            hospital = "Northside Clinic",
            patientName = "Arthur Shelby",
            patientAge = 62,
            patientGender = "Male",
            department = "Neurology",
            priority = Priority.ROUTINE,
            timeLabel = "5 hours ago",
            chiefComplaint = "Persistent headaches and dizziness for 2 weeks.",
            diagnosis = "Tension headache vs chronic migraine."
        )
    )

    override fun getReferrals(): Flow<List<Referral>> = flowOf(mockData)

    override fun getReferralById(id: String): Referral? {
        return mockData.find { it.id == id }
    }

    override suspend fun deleteReferral(id: String) {}
}
