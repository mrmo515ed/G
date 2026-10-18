package com.animeblack.app.data.model

import com.google.firebase.firestore.PropertyName

data class Anime(
    val id: String = "",
    val title: String = "",
    val titleAr: String = "",
    val poster: String = "",
    val cover: String = "",
    val score: Double = 0.0,
    val type: String = "TV",
    val status: String = "مستمر",
    val year: Int = 2024,
    val season: String = "ربيع 2024",
    val episodesCount: Int = 0,
    val genres: List<String> = emptyList(),
    val synopsis: String = "",
    val studio: String = "",
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val id: String = "",
    val number: Int = 1,
    val title: String = "",
    val duration: String = "24:00",
    val thumbnail: String = "",
    val streamUrls: Map<String, String> = emptyMap(), // Quality -> HLS or MP4 url
    val defaultUrl: String = "",
    val introStart: Long = 0,
    val introEnd: Long = 0
)

data class Manga(
    val id: String = "",
    val title: String = "",
    val titleAr: String = "",
    val cover: String = "",
    val score: Double = 0.0,
    val status: String = "مستمر",
    val author: String = "",
    val chaptersCount: Int = 0,
    val genres: List<String> = emptyList(),
    val synopsis: String = "",
    val chapters: List<MangaChapter> = emptyList()
)

data class MangaChapter(
    val id: String = "",
    val number: Int = 1,
    val title: String = "",
    val releaseDate: String = "",
    val pages: List<String> = emptyList()
)

data class Reel(
    val id: String = "",
    val videoUrl: String = "",
    val title: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val soundTitle: String = "Original Otaku Sound",
    val animeTag: String = "Anime Black"
)

data class Quest(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val rewardCoins: Int = 50,
    val rewardXp: Int = 100,
    val targetCount: Int = 1,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val icon: String = "🎯"
)

data class StoreItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val type: String = "card", // card, frame, title, box
    val priceCoins: Long = 100,
    val priceStars: Long = 0,
    val rarity: String = "rare", // common, rare, epic, legendary
    val previewUrl: String = "",
    val icon: String = "🎁"
)

data class WatchHistoryItem(
    val animeId: String = "",
    val animeTitle: String = "",
    val episodeId: String = "",
    val episodeNumber: Int = 1,
    val thumbnail: String = "",
    val progressMillis: Long = 0,
    val durationMillis: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

data class ScheduleItem(
    val dayOfWeek: String = "الأحد",
    val time: String = "18:00",
    val animeId: String = "",
    val animeTitle: String = "",
    val episodeNumber: Int = 1,
    val poster: String = ""
)

data class SecuritySession(
    val sessionId: String = "",
    val userId: String = "",
    val deviceName: String = "",
    val ipAddress: String = "10.0.0.1",
    val isCurrent: Boolean = false,
    val lastActive: Long = System.currentTimeMillis()
)

data class User(
    val id: String = "",
    val uid: String = "",
    val name: String = "أوتاكو",
    val username: String = "otaku",
    val email: String = "",
    val avatar: String = "",
    val bio: String = "عاشق للأنمي والمانجا",
    val role: String = "Member",
    val level: Int = 1,
    val xp: Long = 0,
    val coins: Long = 100,
    val stars: Long = 10,
    val reputation: Long = 15,
    val followers: Int = 0,
    val following: Int = 0,
    val isVerified: Boolean = false,
    val joined: Long = System.currentTimeMillis()
)

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val likedUsers: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Story(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "image",
    val caption: String = "",
    val duration: Int = 15,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000
)

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val text: String = "",
    val mediaUrl: String? = null,
    val mediaType: String = "none",
    val timestamp: Long = System.currentTimeMillis()
)

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val avatar: String = "",
    val creatorId: String = "",
    val privacy: String = "عام",
    val memberUids: List<String> = emptyList(),
    val membersCount: Int = 0,
    val lastMsg: String = "",
    val lastAt: Long = System.currentTimeMillis()
)

data class Rating(
    val id: String = "",
    val animeId: String = "",
    val userId: String = "",
    val userName: String = "",
    val stars: Int = 5,
    val review: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
