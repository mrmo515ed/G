package com.animeblack.app.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.model.Episode
import com.animeblack.app.data.repository.AnimeRepository
import com.animeblack.app.player.AnimePlayerView
import com.animeblack.app.ui.theme.*

@Composable
fun PlayerScreen(
    animeId: String,
    episodeId: String,
    animeRepository: AnimeRepository,
    onBack: () -> Unit = {},
    onSelectEpisode: (String) -> Unit = {}
) {
    val anime = remember(animeId) { animeRepository.getAnimeById(animeId) }
    var currentEpisodeId by remember { mutableStateOf(episodeId) }

    val currentEpisode = remember(currentEpisodeId, anime) {
        anime?.episodes?.find { it.id == currentEpisodeId } ?: anime?.episodes?.firstOrNull()
    }

    var currentStreamUrl by remember(currentEpisode) {
        mutableStateOf(currentEpisode?.defaultUrl ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // ExoPlayer Video View
        if (currentEpisode != null && currentStreamUrl.isNotEmpty()) {
            AnimePlayerView(
                videoUrl = currentStreamUrl,
                title = "${anime?.titleAr ?: ""} - الحلقة ${currentEpisode.number}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                qualities = currentEpisode.streamUrls,
                onQualitySelected = { newUrl -> currentStreamUrl = newUrl },
                onBack = onBack
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentRed)
            }
        }

        // Details & Episode List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = anime?.titleAr ?: "مشغل الأنمي",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "الحلقة ${currentEpisode?.number ?: 1}: ${currentEpisode?.title ?: ""}",
                        color = AccentGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "· ${currentEpisode?.duration ?: "24:00"}",
                        color = TextSecondary,
                        fontSize = 12.sp
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "كاش أوفلاين نشط",
                        tint = AccentEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "محفوظ في الكاش التلقائي للمشاهدة بدون إنترنت",
                        color = AccentEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Actions row (Like, Save, Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = {}) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ThumbUp, contentDescription = "إعجاب", tint = TextPrimary)
                            Text("إعجاب", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = {}) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Bookmark, contentDescription = "حفظ", tint = TextPrimary)
                            Text("قائمتي", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = {}) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = TextPrimary)
                            Text("مشاركة", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = {}) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Download, contentDescription = "تحميل", tint = TextPrimary)
                            Text("تحميل", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "جميع الحلقات (${anime?.episodes?.size ?: 0})",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Episode List
            items(anime?.episodes ?: emptyList()) { ep ->
                val isCurrent = ep.id == currentEpisode?.id
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCurrent) AccentRed.copy(alpha = 0.15f) else DarkCard,
                    border = if (isCurrent) ButtonDefaults.outlinedButtonBorder else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            currentEpisodeId = ep.id
                            currentStreamUrl = ep.defaultUrl
                            onSelectEpisode(ep.id)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${ep.number}",
                            color = if (isCurrent) AccentRed else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.width(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ep.title,
                                color = if (isCurrent) Color.White else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = ep.duration,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (isCurrent) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = AccentRed)
                        }
                    }
                }
            }
        }
    }
}
