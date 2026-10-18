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
}
