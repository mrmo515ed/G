package com.animeblack.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.animeblack.app.data.repository.AnimeRepository
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.components.EmptyStateView
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.theme.*

@Composable
fun LibraryScreen(
    animeRepository: AnimeRepository,
    firebaseRepository: FirebaseRepository,
    onAnimeClick: (String) -> Unit,
    onPlayEpisode: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("المفضلة", "سجل المشاهدة", "التحميلات أوفلاين")

    val currentUid = firebaseRepository.currentUserId ?: "guest_user"
    val favoritesIds by firebaseRepository.observeFavorites(currentUid).collectAsState(initial = emptyList())
    val watchHistory by firebaseRepository.observeWatchHistory(currentUid).collectAsState(initial = emptyList())

    val allAnime = animeRepository.getFeaturedAnime()
    val favoriteAnimeList = allAnime.filter { it.id in favoritesIds || favoritesIds.isEmpty() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spaceLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
            }
            Text(
                text = "مكتبتي",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = Dimensions.spaceSm)
            )
        }

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkCharcoal,
            contentColor = CyberBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyberBlue
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) CyberBlue else TextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spaceMd))

        when (selectedTab) {
            0 -> {
                // Favorites
                if (favoriteAnimeList.isEmpty()) {
                    EmptyStateView(
                        title = "لا توجد أنميات في المفضلة",
                        description = "أضف الأنميات المفضلة لديك للوصول إليها بسرعة في أي وقت",
                        icon = Icons.Default.FavoriteBorder
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(Dimensions.spaceLg),
                        horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceMd),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spaceMd)
                    ) {
                        items(favoriteAnimeList) { anime ->
                            GlassCard(
                                onClick = { onAnimeClick(anime.id) },
                                cornerRadius = Dimensions.radiusMd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(Dimensions.radiusSm))
                                ) {
                                    AsyncImage(
                                        model = anime.poster,
                                        contentDescription = anime.titleAr,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                                Text(
                                    text = anime.titleAr,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${anime.episodesCount} حلقة • ${anime.type}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Watch History
                if (watchHistory.isEmpty()) {
                    EmptyStateView(
                        title = "سجل المشاهدة فارغ",
                        description = "ابدأ بمشاهدة حلقات الأنمي لتسجيل تقدمك ومتابعة المشاهدة من حيث توقفت",
                        icon = Icons.Default.History
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(Dimensions.spaceLg),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spaceMd)
                    ) {
                        items(watchHistory) { history ->
                            GlassCard(
                                onClick = { onPlayEpisode(history.animeId, history.episodeId) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp, 55.dp)
                                            .clip(RoundedCornerShape(Dimensions.radiusSm))
                                    ) {
                                        AsyncImage(
                                            model = history.thumbnail,
                                            contentDescription = history.animeTitle,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = history.animeTitle,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "الحلقة ${history.episodeNumber}",
                                            color = CyberBlue,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Icon(
                                        Icons.Default.PlayCircleOutline,
                                        contentDescription = "تشغيل",
                                        tint = CyberBlue,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Offline Downloads (Media3 ExoPlayer cache)
                EmptyStateView(
                    title = "الكاش التلقائي مفعل",
                    description = "تطبيق أندرويد يحفظ الحلقات التي تمت مشاهدتها تلقائياً عبر ExoPlayer Cache للتشغيل بدون إنترنت.",
                    icon = Icons.Default.DownloadDone,
                    actionText = "تصفح الأنميات للتحميل",
                    onAction = onBack
                )
            }
        }
    }
}
