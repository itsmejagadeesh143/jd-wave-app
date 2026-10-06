package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class JDWaveFirestoreRepository(val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: com.google.firebase.auth.FirebaseAuth
        get() = com.google.firebase.auth.FirebaseAuth.getInstance(db.app)

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun syncUserProfile(user: FirebaseUser, onComplete: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val uid = user.uid
        val userDocRef = db.collection("users").document(uid)
        val path = userDocRef.path
        userDocRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val data = mutableMapOf<String, Any>(
                    "userId" to uid,
                    "email" to (user.email ?: ""),
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                user.displayName?.let { data["displayName"] = it }
                user.photoUrl?.let { data["photoUrl"] = it.toString() }
                userDocRef.set(data)
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { e ->
                        val err = handleFirestoreError(e, OperationType.CREATE, path)
                        onError(err)
                    }
            } else {
                val updates = mutableMapOf<String, Any>(
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                user.displayName?.let { updates["displayName"] = it }
                user.photoUrl?.let { updates["photoUrl"] = it.toString() }
                userDocRef.update(updates)
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { e ->
                        val err = handleFirestoreError(e, OperationType.UPDATE, path)
                        onError(err)
                    }
            }
        }.addOnFailureListener { e ->
            val err = handleFirestoreError(e, OperationType.GET, path)
            onError(err)
        }
    }

    fun observePlaybackProgress(): Flow<List<PlaybackProgressFirestore>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/progress"
        emitAll(
            db.collection("users").document(uid).collection("progress")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(PlaybackProgressFirestore::class.java)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun savePlaybackProgress(
        storyId: String,
        episodeId: String,
        positionMs: Long,
        durationMs: Long,
        completed: Boolean,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("progress").document(episodeId)
        val path = docRef.path
        val payload = mapOf(
            "userId" to uid,
            "storyId" to storyId,
            "episodeId" to episodeId,
            "positionMs" to positionMs.coerceAtLeast(0L),
            "durationMs" to durationMs.coerceAtLeast(0L),
            "completed" to completed,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.WRITE, path)
                onError(e.localizedMessage ?: "Failed to save playback progress")
            }
    }

    fun observeFavorites(): Flow<List<String>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/favorites"
        emitAll(
            db.collection("users").document(uid).collection("favorites")
                .snapshots()
                .map { snapshot ->
                    snapshot.documents.mapNotNull { it.getString("storyId") }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun toggleFavorite(storyId: String, add: Boolean, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("favorites").document(storyId)
        val path = docRef.path
        if (add) {
            val payload = mapOf(
                "userId" to uid,
                "storyId" to storyId,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.CREATE, path)
                    onError(e.localizedMessage ?: "Failed to add favorite")
                }
        } else {
            docRef.delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    handleFirestoreError(e, OperationType.DELETE, path)
                    onError(e.localizedMessage ?: "Failed to remove favorite")
                }
        }
    }

    fun observeBookmarks(): Flow<List<BookmarkFirestore>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/bookmarks"
        emitAll(
            db.collection("users").document(uid).collection("bookmarks")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(BookmarkFirestore::class.java)
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    fun addBookmark(
        storyId: String,
        episodeId: String,
        positionMs: Long,
        note: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val uid = requireUserId()
        val bookmarkId = "bm_${System.currentTimeMillis()}"
        val docRef = db.collection("users").document(uid).collection("bookmarks").document(bookmarkId)
        val path = docRef.path
        val payload = mapOf(
            "userId" to uid,
            "storyId" to storyId,
            "episodeId" to episodeId,
            "positionMs" to positionMs.coerceAtLeast(0L),
            "note" to note.take(500),
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.CREATE, path)
                onError(e.localizedMessage ?: "Failed to add bookmark")
            }
    }

    fun deleteBookmark(bookmarkId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("bookmarks").document(bookmarkId)
        val path = docRef.path
        docRef.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.DELETE, path)
                onError(e.localizedMessage ?: "Failed to delete bookmark")
            }
    }

    /**
     * Observes real-time admin Daily Free configuration stored at admin_configs/daily_free
     */
    fun observeDailyFreeConfig(): Flow<DailyFreeFirestoreConfig?> = flow {
        val path = "admin_configs/daily_free"
        emitAll(
            db.collection("admin_configs").document("daily_free")
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(DailyFreeFirestoreConfig::class.java)
                    } else {
                        null
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    emit(null)
                }
        )
    }

    /**
     * Persists Daily Free configuration to Firestore at admin_configs/daily_free.
     * Ready for server-side security authorization via rules.
     */
    fun saveDailyFreeConfig(
        config: com.example.model.DailyFreeEpisodeConfig,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val docRef = db.collection("admin_configs").document("daily_free")
        val path = docRef.path
        val payload = mapOf(
            "dateKey" to config.dateKey,
            "storyId" to config.storyId,
            "episodeId" to config.episodeId,
            "storyTitle" to config.storyTitle,
            "episodeTitle" to config.episodeTitle,
            "episodeNumber" to config.episodeNumber,
            "unlockTimeUtcEpochMs" to config.unlockTimeUtcEpochMs,
            "active" to config.active,
            "serverConfigTimestamp" to FieldValue.serverTimestamp()
        )
        docRef.set(payload)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                handleFirestoreError(e, OperationType.WRITE, path)
                onError(e.localizedMessage ?: "Failed to save Daily Free episode configuration")
            }
    }
}
