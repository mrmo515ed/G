package com.animeblack.app.data.repository

import com.animeblack.app.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    // Users Flow
    fun observeUserProfile(userId: String): Flow<User?> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    // Live Posts Feed Flow
    fun observePosts(limitCount: Long = 50): Flow<List<Post>> = callbackFlow {
        val listener = firestore.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limitCount)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    // Live Stories Flow
    fun observeStories(): Flow<List<Story>> = callbackFlow {
        val now = System.currentTimeMillis()
        val listener = firestore.collection("stories")
            .whereGreaterThan("expiresAt", now)
            .orderBy("expiresAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val stories = snapshot?.documents?.mapNotNull { it.toObject(Story::class.java) } ?: emptyList()
                trySend(stories)
            }
        awaitClose { listener.remove() }
    }

    // Live Chat Messages Flow
    fun observeGroupMessages(groupId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("groups").document(groupId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                trySend(msgs)
            }
        awaitClose { listener.remove() }
    }

    // Send Message
    suspend fun sendMessage(groupId: String, message: ChatMessage): Result<Unit> {
        return try {
            firestore.collection("groups").document(groupId)
                .collection("messages").document(message.id)
                .set(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Toggle Post Like
    suspend fun toggleLike(postId: String, userId: String): Result<Unit> {
        return try {
            val docRef = firestore.collection("posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val likedUsers = (snapshot.get("likedUsers") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()
                val currentLikes = snapshot.getLong("likesCount") ?: 0

                if (likedUsers.contains(userId)) {
                    likedUsers.remove(userId)
                    transaction.update(docRef, "likedUsers", likedUsers)
                    transaction.update(docRef, "likesCount", maxOf(0, currentLikes - 1))
                } else {
                    likedUsers.add(userId)
                    transaction.update(docRef, "likedUsers", likedUsers)
                    transaction.update(docRef, "likesCount", currentLikes + 1)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Record User Session
    suspend fun recordSession(sessionId: String, deviceId: String) {
        val uid = currentUserId ?: return
        try {
            val sessionData = hashMapOf(
                "sessionId" to sessionId,
                "userId" to uid,
                "deviceId" to deviceId,
                "startTime" to System.currentTimeMillis(),
                "lastActive" to System.currentTimeMillis()
            )
            firestore.collection("sessions").document(sessionId).set(sessionData).await()
        } catch (e: Exception) {
            // Log locally without crashing
        }
    }

    // Authentication Methods
    suspend fun signInWithEmail(email: String, pass: String): Result<String> {
        return try {
            val res = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = res.user?.uid ?: throw IllegalStateException("User ID is null")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String, displayName: String): Result<String> {
        return try {
            val res = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = res.user?.uid ?: throw IllegalStateException("User ID is null")
            val userProfile = hashMapOf(
                "uid" to uid,
                "email" to email,
                "name" to displayName,
                "username" to displayName.lowercase().replace(" ", "_"),
                "avatar" to "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=200",
                "role" to "Member",
                "level" to 1,
                "coins" to 100,
                "stars" to 10,
                "joined" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(userProfile).await()
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val res = auth.signInAnonymously().await()
            val uid = res.user?.uid ?: throw IllegalStateException("User ID is null")
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // Notifications Flow
    fun observeNotifications(userId: String): Flow<List<NotificationItem>> = callbackFlow {
        val listener = firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject(NotificationItem::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markNotificationRead(notifId: String) {
        try {
            firestore.collection("notifications").document(notifId).update("isRead", true).await()
        } catch (_: Exception) {}
    }

    // Favorites & Library Flow
    fun observeFavorites(userId: String): Flow<List<String>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val favs = (snapshot?.get("favorites") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                trySend(favs)
            }
        awaitClose { listener.remove() }
    }

    suspend fun toggleFavorite(userId: String, animeId: String): Result<Boolean> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            var isAdded = false
            firestore.runTransaction { tx ->
                val snap = tx.get(userRef)
                val favs = (snap.get("favorites") as? List<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()
                if (favs.contains(animeId)) {
                    favs.remove(animeId)
                    isAdded = false
                } else {
                    favs.add(animeId)
                    isAdded = true
                }
                tx.update(userRef, "favorites", favs)
            }.await()
            Result.success(isAdded)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Watch History
    suspend fun saveWatchProgress(userId: String, item: WatchHistoryItem) {
        try {
            firestore.collection("users").document(userId)
                .collection("history").document(item.animeId)
                .set(item).await()
        } catch (_: Exception) {}
    }

    fun observeWatchHistory(userId: String): Flow<List<WatchHistoryItem>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("history")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject(WatchHistoryItem::class.java) } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }
}
