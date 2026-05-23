package csmc.hospital.notifier.data.remote

import csmc.hospital.notifier.data.model.ActivityLogResponse
import csmc.hospital.notifier.data.model.DepartmentResponse
import csmc.hospital.notifier.data.model.ForwardReferralResponse
import csmc.hospital.notifier.data.model.LoginRequest
import csmc.hospital.notifier.data.model.LoginResponse
import csmc.hospital.notifier.data.model.QueueItem
import csmc.hospital.notifier.data.model.QueueListResponse
import csmc.hospital.notifier.data.model.ReferralListResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("get_departments")
    suspend fun getDepartments(): DepartmentResponse

    @GET("get_referrals")
    suspend fun getReferrals(
        @Query("search_key") searchKey: String = "",
        @Query("department") department: String = "",
        @Query("offset") offset: Int = 0
    ): ReferralListResponse

    @GET("get_all_referrals")
    suspend fun getAllReferrals(): ReferralListResponse

    @FormUrlEncoded
    @POST("insert_referral")
    suspend fun insertReferral(
        @Field("userid") userid: Int,
        @Field("firebase_key") firebaseKey: String,
        @Field("patient") patient: String,
        @Field("gender") gender: String,
        @Field("age") age: Int,
        @Field("diagnosis") diagnosis: String,
        @Field("chief_complaint") chiefComplaint: String,
        @Field("department") department: String,
        @Field("code") code: String,
        @Field("referring_hospital") referringHospital: String,
        @Field("remarks") remarks: String,
        @Field("date_created") dateCreated: String
    ): ForwardReferralResponse

    @FormUrlEncoded
    @POST("forward_referral")
    suspend fun forwardReferral(
        @Field("firebase_key") firebaseKey: String,
        @Field("forwarded_by") forwardedBy: Int,
        @Field("origin") origin: String,
        @Field("destination") destination: String,
        @Field("code") code: String,
        @Field("remarks") remarks: String
    ): ForwardReferralResponse

    @FormUrlEncoded
    @POST("tag_as_done")
    suspend fun tagAsDone(
        @Field("firebase_key") firebaseKey: String,
        @Field("userid") userid: Int,
        @Field("remarks") remarks: String
    ): ForwardReferralResponse

    @GET("queue_list")
    suspend fun getQueueList(): QueueListResponse

    @FormUrlEncoded
    @POST("queue_referral")
    suspend fun queueReferral(
        @Field("referring_hospital") referringHospital: String,
        @Field("patient") patient: String,
        @Field("age") age: Int,
        @Field("gender") gender: String,
        @Field("department") department: String,
        @Field("userid") userid: Int,
        @Field("remarks") remarks: String,
        @Field("diagnosis") diagnosis: String,
        @Field("chief_complaint") chiefComplaint: String
    ): ForwardReferralResponse

    @GET("load_logs")
    suspend fun getActivityLogs(
        @Query("firebase_key") firebaseKey: String
    ): ActivityLogResponse

    @FormUrlEncoded
    @PUT("change_password")
    suspend fun changePassword(
        @Field("id") id: Int,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): ForwardReferralResponse
}
