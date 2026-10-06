package com.example.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

data class UserFirestoreProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class PlaybackProgressFirestore(
    val userId: String = "",
    val storyId: String = "",
    val episodeId: String = "",
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val completed: Boolean = false,
    val updatedAt: Timestamp? = null
)

data class FavoriteStoryFirestore(
    val userId: String = "",
    val storyId: String = "",
    val createdAt: Timestamp? = null
)

data class BookmarkFirestore(
    val userId: String = "",
    val storyId: String = "",
    val episodeId: String = "",
    val positionMs: Long = 0L,
    val note: String = "",
    val createdAt: Timestamp? = null
)

data class DailyFreeFirestoreConfig(
    val dateKey: String = "",
    val storyId: String = "",
    val episodeId: String = "",
    val storyTitle: String = "",
    val episodeTitle: String = "",
    val episodeNumber: Int = 1,
    val unlockTimeUtcEpochMs: Long = 0L,
    val active: Boolean = true,
    val serverConfigTimestamp: Timestamp? = null
)
