package com.animeblack.app.ui.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.animeblack.app.data.model.Post
import com.animeblack.app.data.model.Story
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(
    firebaseRepository: FirebaseRepository
) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    val currentUserId = firebaseRepository.currentUserId ?: "guest"

    LaunchedEffect(Unit) {
        firebaseRepository.observePosts().collect { list ->
            posts = list
        }
    }

    LaunchedEffect(Unit) {
        firebaseRepository.observeStories().collect { list ->
            stories = list
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Community Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مجتمع أوتاكو أنمي بلاك",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {}) {
                    Icon(Icons.Default.AddCircle, contentDescription = "منشور جديد", tint = AccentRed)
                }
            }
        }

        // Stories Bar
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add your story button
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = AccentGold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("قصتك", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                // Community stories
                items(stories) { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(listOf(AccentRed, AccentGold, AccentCyan, AccentRed))
                                )
                                .padding(2.5.dp)
                        ) {
                            AsyncImage(
                                model = story.userAvatar.ifEmpty { story.mediaUrl },
                                contentDescription = story.userName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = story.userName,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Posts Feed with Glassmorphism
        items(posts) { post ->
            val isLiked = post.likedUsers.contains(currentUserId)
            com.animeblack.app.ui.components.GlassCard(
                cornerRadius = 14.dp,
                borderAlpha = 0.15f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                    // Author Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = post.authorAvatar,
                            contentDescription = post.authorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = post.authorName,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "منذ قليل",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Content
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = post.content,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    // Post Image if available
                    if (!post.mediaUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AsyncImage(
                            model = post.mediaUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    // Actions Bar (Likes, Comments)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    firebaseRepository.toggleLike(post.id, currentUserId)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "إعجاب",
                                tint = if (isLiked) AccentRed else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${post.likesCount}",
                                color = if (isLiked) AccentRed else TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { }
                        ) {
                            Icon(
                                Icons.Default.ChatBubbleOutline,
                                contentDescription = "تعليق",
                                tint = TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${post.commentsCount}",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "مشاركة",
                                tint = TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
