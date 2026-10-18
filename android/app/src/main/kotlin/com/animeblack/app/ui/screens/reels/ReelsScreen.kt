package com.animeblack.app.ui.screens.reels

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.animeblack.app.data.model.Reel
import com.animeblack.app.ui.theme.AccentGold
import com.animeblack.app.ui.theme.AccentRed
import com.animeblack.app.ui.theme.DarkBg

@OptIn(UnstableApi::class)
@ExperimentalFoundationApi
@Composable
fun ReelsScreen() {
    val sampleReels = remember {
        listOf(
            Reel(
                id = "r1",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                title = "أعظم لقطة في تاريخ الأنمي! قتال غوجو وسوكونا الملحمي 🔥",
                authorName = "ساتورو غوجو",
                authorAvatar = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=200",
                likesCount = 1420,
                commentsCount = 89,
                soundTitle = "Jujutsu Kaisen OST - Specialz",
                animeTag = "جوجوتسو كايسن"
            ),
            Reel(
                id = "r2",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                title = "هجوم ليفاي الأسطوري على العملاق القرد بلا رحمة ⚔️",
                authorName = "ليفاي أكرمان",
                authorAvatar = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=200",
                likesCount = 2890,
                commentsCount = 215,
                soundTitle = "Attack on Titan - Ashes on the Fire",
                animeTag = "هجوم العمالقة"
            ),
            Reel(
                id = "r3",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                title = "استيقاظ جين وو الأسطوري وجيش الظلال: انهض! ⚡",
                authorName = "سونغ جين وو",
                authorAvatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=200",
                likesCount = 3450,
                commentsCount = 310,
                soundTitle = "Solo Leveling - Dark Aria",
                animeTag = "سولو ليفلينج"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { sampleReels.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ReelPlayerItem(
                reel = sampleReels[page],
                isCurrent = pagerState.currentPage == page
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ReelPlayerItem(
    reel: Reel,
    isCurrent: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLiked by remember { mutableStateOf(false) }
    var likes by remember { mutableIntStateOf(reel.likesCount) }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    LaunchedEffect(isCurrent, reel.videoUrl) {
        if (isCurrent) {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(reel.videoUrl)))
            exoPlayer.prepare()
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> if (isCurrent) exoPlayer.play()
                Lifecycle.Event.ON_DESTROY -> exoPlayer.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 600f
                    )
                )
        )

        // Right Action Bar (Likes, Comments, Share, Sound)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Author Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AccentRed)
                    .padding(2.dp)
            ) {
                AsyncImage(
                    model = reel.authorAvatar,
                    contentDescription = reel.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }

            // Like Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    isLiked = !isLiked
                    likes += if (isLiked) 1 else -1
                }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "إعجاب",
                    tint = if (isLiked) AccentRed else Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Text("$likes", color = Color.White, fontSize = 12.sp)
            }

            // Comments Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = "تعليقات", tint = Color.White, modifier = Modifier.size(30.dp))
                Text("${reel.commentsCount}", color = Color.White, fontSize = 12.sp)
            }

            // Share Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = Color.White, modifier = Modifier.size(30.dp))
                Text("مشاركة", color = Color.White, fontSize = 11.sp)
            }
        }

        // Bottom Details (Author, Title, Sound)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.8f)
                .padding(start = 16.dp, bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${reel.authorName}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = AccentRed
                ) {
                    Text(
                        text = reel.animeTag,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = reel.title,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reel.soundTitle,
                    color = AccentGold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
