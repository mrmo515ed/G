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
import com.animeblack.app.data.repository.AnimeRepository
import com.animeblack.app.ui.theme.*

@Composable
fun HomeScreen(
    animeRepository: AnimeRepository,
    onAnimeClick: (String) -> Unit = {},
    onEpisodeClick: (String, String) -> Unit = { _, _ -> }
) {
    var animeList by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var selectedGenre by remember { mutableStateOf("الكل") }
    val genres = listOf("الكل", "أكشن", "شونين", "دراما", "فانتازيا", "غموض", "خوارق")

    LaunchedEffect(Unit) {
        animeRepository.getTrendingAnime().collect { list ->
            animeList = list
        }
    }

    val filteredList = remember(selectedGenre, animeList) {
        if (selectedGenre == "الكل") animeList
        else animeList.filter { it.genres.contains(selectedGenre) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // App Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(listOf(AccentRed, AccentGold))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "أنمي بلاك",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "منصة الأوتاكو الأولى",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                var showSearchDialog by remember { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }
                var searchResult by remember { mutableStateOf("") }
                var isSearching by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()
                val apiClient = remember { com.animeblack.app.data.network.ApiClient() }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "بحث الذكاء الاصطناعي", tint = TextPrimary)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = "تنبيهات", tint = TextPrimary)
                    }
                }

                if (showSearchDialog) {
                    AlertDialog(
                        onDismissRequest = { showSearchDialog = false },
                        title = { Text("بحث الأنمي الذكي (Gemini)", fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("ابحث عن أي أنمي أو اسأل عن توصيات...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )
                                if (isSearching) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        CircularProgressIndicator(color = AccentRed, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("جاري البحث في قاعدة البيانات...", fontSize = 12.sp)
                                    }
                                } else if (searchResult.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(14.dp))
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
                                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                            ) {
                                Text("بحث")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showSearchDialog = false }) {
                                Text("إغلاق")
                            }
                        }
                    )
                }
            }
        }

        // Featured Hero Banner
        if (animeList.isNotEmpty()) {
            val featured = animeList.first()
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
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
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, DarkBg.copy(alpha = 0.95f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "★ ${featured.score}",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "· ${featured.type} · ${featured.season}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = featured.titleAr,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (featured.episodes.isNotEmpty()) {
                                    onEpisodeClick(featured.id, featured.episodes.first().id)
                                } else {
                                    onAnimeClick(featured.id)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("شاهد الحلقة الأولى", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Genre Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres) { g ->
                    val isSelected = selectedGenre == g
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AccentRed else DarkSurface,
                        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null,
                        modifier = Modifier.clickable { selectedGenre = g }
                    ) {
                        Text(
                            text = g,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    color = AccentGold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { }
                )
            }
        }

        // Anime Horizontal Scroll Cards
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { anime ->
                    Column(
                        modifier = Modifier
                            .width(140.dp)
                            .clickable { onAnimeClick(anime.id) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = anime.poster,
                                contentDescription = anime.titleAr,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                shape = RoundedCornerShape(bottomStart = 8.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = "★ ${anime.score}",
                                    color = AccentGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
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
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "أحدث الحلقات المضافة",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Episodes Grid List with Glassmorphism
        items(animeList.flatMap { a -> a.episodes.map { ep -> Pair(a, ep) } }) { (anime, ep) ->
            com.animeblack.app.ui.components.GlassCard(
                cornerRadius = 12.dp,
                borderAlpha = 0.15f,
                onClick = { onEpisodeClick(anime.id, ep.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(110.dp)
                            .height(66.dp)
                            .clip(RoundedCornerShape(8.dp))
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
                                .background(Color.Black.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = anime.titleAr,
                            color = AccentGold,
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "· تشغيل أوفلاين متاح",
                                color = AccentEmerald,
                                fontSize = 10.sp
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
