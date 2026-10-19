package com.animeblack.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.animeblack.app.data.model.Anime
import com.animeblack.app.data.network.ApiClient
import com.animeblack.app.data.repository.AnimeRepository
import com.animeblack.app.ui.components.AnimeGridSkeleton
import com.animeblack.app.ui.components.CyberButton
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.components.OtakuBadge
import com.animeblack.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    animeRepository: AnimeRepository,
    onAnimeClick: (String) -> Unit = {},
    onEpisodeClick: (String, String) -> Unit = { _, _ -> }
) {
    var animeList by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedGenre by remember { mutableStateOf("الكل") }
    val genres = listOf("الكل", "أكشن", "شونين", "دراما", "فانتازيا", "غموض", "خوارق")

    val coroutineScope = rememberCoroutineScope()
    val apiClient = remember { ApiClient() }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animeRepository.getTrendingAnime().collect { list ->
            animeList = list
            isLoading = false
        }
    }

    val filteredList = remember(selectedGenre, animeList) {
        if (selectedGenre == "الكل") animeList
        else animeList.filter { it.genres.contains(selectedGenre) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // App Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceMd),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(Dimensions.radiusMd))
                            .background(GradCyberBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                    Column {
                        Text(
                            text = "أنمي بلاك",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "منصة الأوتاكو الأولى",
                            color = CyberBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSm)) {
                    IconButton(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier.size(Dimensions.iconButtonSize)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "بحث الذكاء الاصطناعي", tint = TextPrimary)
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(Dimensions.iconButtonSize)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "تنبيهات", tint = TextPrimary)
                    }
                }
            }
        }

        // Search Dialog with Gemini Search Agent
        if (showSearchDialog) {
            item {
                AlertDialog(
                    onDismissRequest = { showSearchDialog = false },
                    title = {
                        Text(
                            "بحث الأنمي الذكي (Gemini)",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    },
                    containerColor = DarkCharcoal,
                    text = {
                        Column {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("ابحث عن أي أنمي أو اطلب توصيات...", color = TextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CyberBlue,
                                    unfocusedBorderColor = DarkBorder
                                ),
                                maxLines = 2
                            )
                            if (isSearching) {
                                Spacer(modifier = Modifier.height(Dimensions.spaceLg))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator(color = CyberBlue, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                                    Text("جاري فحص قاعدة البيانات...", color = TextSecondary, fontSize = 12.sp)
                                }
                            } else if (searchResult.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(Dimensions.spaceLg))
                                Text(
                                    text = searchResult,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    isSearching = true
                                    coroutineScope.launch {
                                        searchResult = apiClient.askGeminiAgent(searchQuery)
                                        isSearching = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                        ) {
                            Text("بحث", color = AmoledBlack, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSearchDialog = false }) {
                            Text("إغلاق", color = TextSecondary)
                        }
                    }
                )
            }
        }

        // Shimmer Loading Skeleton
        if (isLoading) {
            item {
                Spacer(modifier = Modifier.height(Dimensions.spaceMd))
                AnimeGridSkeleton()
                Spacer(modifier = Modifier.height(Dimensions.spaceXl))
                AnimeGridSkeleton()
            }
        } else {
            // Featured Hero Banner
            if (animeList.isNotEmpty()) {
                val featured = animeList.first()
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceSm)
                            .clip(RoundedCornerShape(Dimensions.radiusLg))
                            .clickable { onAnimeClick(featured.id) }
                    ) {
                        AsyncImage(
                            model = featured.cover,
                            contentDescription = featured.titleAr,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GradCardOverlay)
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(Dimensions.spaceLg)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OtakuBadge(text = "★ ${featured.score}", color = CyberBlue)
                                Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                                Text(
                                    text = "· ${featured.type} · ${featured.season}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(Dimensions.spaceXs))
                            Text(
                                text = featured.titleAr,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                            CyberButton(
                                text = "شاهد الحلقة الأولى",
                                onClick = {
                                    if (featured.episodes.isNotEmpty()) {
                                        onEpisodeClick(featured.id, featured.episodes.first().id)
                                    } else {
                                        onAnimeClick(featured.id)
                                    }
                                },
                                icon = {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Genre Filter Chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimensions.spaceMd),
                    contentPadding = PaddingValues(horizontal = Dimensions.spaceLg),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSm)
                ) {
                    items(genres) { g ->
                        val isSelected = selectedGenre == g
                        Surface(
                            shape = RoundedCornerShape(Dimensions.radiusFull),
                            color = if (isSelected) CyberBlue else DarkCharcoal,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                            modifier = Modifier
                                .heightIn(min = 36.dp)
                                .clickable { selectedGenre = g }
                        ) {
                            Text(
                                text = g,
                                color = if (isSelected) AmoledBlack else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceSm)
                            )
                        }
                    }
                }
            }

            // Trending Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceSm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الأكثر شعبية هذا الأسبوع",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "عرض الكل",
                        color = CyberBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            // Anime Horizontal Scroll Cards
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = Dimensions.spaceLg),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceMd)
                ) {
                    items(filteredList) { anime ->
                        Column(
                            modifier = Modifier
                                .width(Dimensions.animePosterWidth)
                                .clickable { onAnimeClick(anime.id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimensions.animePosterHeight)
                                    .clip(RoundedCornerShape(Dimensions.radiusLg))
                            ) {
                                AsyncImage(
                                    model = anime.poster,
                                    contentDescription = anime.titleAr,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = RoundedCornerShape(bottomStart = Dimensions.radiusSm),
                                    color = AmoledBlack.copy(alpha = 0.85f),
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(
                                        text = "★ ${anime.score}",
                                        color = CyberBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = Dimensions.spaceSm, vertical = Dimensions.space2xs)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                            Text(
                                text = anime.titleAr,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "${anime.episodesCount} حلقة · ${anime.status}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Latest Episodes Section
            item {
                Spacer(modifier = Modifier.height(Dimensions.spaceXl))
                Text(
                    text = "أحدث الحلقات المضافة",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceSm)
                )
            }

            // Episodes Grid List with Glassmorphism
            items(animeList.flatMap { a -> a.episodes.map { ep -> Pair(a, ep) } }) { (anime, ep) ->
                GlassCard(
                    cornerRadius = Dimensions.radiusLg,
                    borderAlpha = 0.15f,
                    onClick = { onEpisodeClick(anime.id, ep.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceXs)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(Dimensions.episodeThumbWidth)
                                .height(Dimensions.episodeThumbHeight)
                                .clip(RoundedCornerShape(Dimensions.radiusSm))
                        ) {
                            AsyncImage(
                                model = ep.thumbnail,
                                contentDescription = ep.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = anime.titleAr,
                                color = CyberBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "الحلقة ${ep.number}: ${ep.title}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ep.duration,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                                Text(
                                    text = "· تشغيل أوفلاين متاح",
                                    color = StatusSuccess,
                                    fontSize = 10.sp
                                )
                            }
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
