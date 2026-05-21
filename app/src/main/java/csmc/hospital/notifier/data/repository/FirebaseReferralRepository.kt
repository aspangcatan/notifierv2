package csmc.hospital.notifier.data.repository

import csmc.hospital.notifier.data.model.Referral
import csmc.hospital.notifier.data.model.ReferralFirebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseReferralRepository(
    private val databasePath: String = "23"
) : ReferralRepository {

    private val reference = FirebaseDatabase.getInstance().getReference(databasePath)

    override fun getReferrals(): Flow<List<Referral>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val referrals = snapshot.children.mapNotNull { child ->
                    child.getValue(ReferralFirebase::class.java)
                        ?.toDomain(child.key.orEmpty())
                }
                trySend(referrals)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    override fun getReferralById(id: String): Referral? = null

    override suspend fun deleteReferral(id: String) {
        suspendCancellableCoroutine { continuation ->
            reference.child(id).removeValue()
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }
}
