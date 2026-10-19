package com.animeblack.app.ui.screens.manga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.animeblack.app.data.model.Manga
import com.animeblack.app.data.model.MangaChapter
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.components.OtakuBadge
import com.animeblack.app.ui.theme.*

@Composable
fun MangaScreen(
    onBack: () -> Unit = {}
) {
    val sampleManga = remember {
        listOf(
            Manga(
                id = "m1",
                title = "One Piece",
                titleAr = "ون بيس",
                cover = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
                score = 9.2,
                status = "مستمر",
                author = "إييتشيرو أودا",
                chaptersCount = 1110,
                genres = listOf("أكشن", "مغامرات", "كوميديا"),
                synopsis = "مونكي دي لوفي ينطلق في رحلة ملحمية عبر البحار لجمع طاقمه والعثور على كنز الون بيس ليصبح ملك القراصنة.",
                chapters = listOf(
                    MangaChapter(
                        id = "c1110",
                        number = 1110,
                        title = "هبوط الكواكب الخمسة",
                        releaseDate = "منذ يومين",
                        pages = listOf(
                            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1000",
                            "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1000",
                            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1000"
                        )
                    ),
                    MangaChapter(
                        id = "c1109",
                        number = 1109,
                        title = "بث فيغابانك العالمي",
                        releaseDate = "منذ أسبوع",
                        pages = listOf(
                            "https://images.unsplash.com/photo-1563089145-599997674d42?w=1000"
                        )
                    )
                )
            ),
            Manga(
                id = "m2",
                title = "Chainsaw Man",
                titleAr = "رجل المنشار",
                cover = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600",
                score = 8.7,
                status = "مستمر",
                author = "تاتسوكي فوجيموتو",
                chaptersCount = 160,
                genres = listOf("أكشن", "خوارق", "رعب"),
                synopsis = "دنجي شاب فقير يعيش حياة بائسة يسدد ديون والده حتى يندمج مع شيطان المنشار بوتشيتا ليبدأ حياة جديدة كصياد شياطين.",
                chapters = listOf(
                    MangaChapter(
                        id = "c160",
                        number = 160,
                        title = "عالم الشياطين",
                        releaseDate = "منذ 3 أيام",
                        pages = listOf("https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1000")
                    )
                )
            )
        )
    }

    var selectedManga by remember { mutableStateOf<Manga?>(null) }
    var activeChapter by remember { mutableStateOf<MangaChapter?>(null) }

    if (activeChapter != null) {
        // Fullscreen Manga Reader
        MangaReaderView(
            chapter = activeChapter!!,
            onClose = { activeChapter = null }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AmoledBlack)
                .padding(Dimensions.spaceLg)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedManga != null) {
                        IconButton(
                            onClick = { selectedManga = null },
                            modifier = Modifier.size(Dimensions.iconButtonSize)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                        }
                    }
                    Text(
                        text = if (selectedManga != null) selectedManga!!.titleAr else "مكتبة المانجا والويب تون",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(Dimensions.spaceLg))
            }

            if (selectedManga == null) {
                // Manga Catalog List with GlassCard
                items(sampleManga) { manga ->
                    GlassCard(
                        cornerRadius = Dimensions.radiusLg,
                        borderAlpha = 0.12f,
                        onClick = { selectedManga = manga },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimensions.spaceXs)
                    ) {
                        Row {
                            AsyncImage(
                                model = manga.cover,
                                contentDescription = manga.titleAr,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(115.dp)
                                    .clip(RoundedCornerShape(Dimensions.radiusSm))
                            )
                            Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = manga.titleAr,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(Dimensions.space2xs))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "المؤلف: ${manga.author}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                                    OtakuBadge(text = "★ ${manga.score}", color = CyberBlue)
                                }
                                Spacer(modifier = Modifier.height(Dimensions.spaceXs))
                                Text(
                                    text = manga.synopsis,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                                Text(
                                    text = "${manga.chaptersCount} فصل · ${manga.status}",
                                    color = ElectricPurpleLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                // Chapters list of selected manga
                item {
                    Text(
                        text = "الفصول المتاحة للقراءة",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                }

                items(selectedManga!!.chapters) { chapter ->
                    GlassCard(
                        cornerRadius = Dimensions.radiusMd,
                        onClick = { activeChapter = chapter },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimensions.spaceXs)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = Dimensions.minTouchTarget),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "الفصل ${chapter.number}: ${chapter.title}",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = chapter.releaseDate,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Icon(Icons.Default.MenuBook, contentDescription = "قراءة", tint = CyberBlue)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun MangaReaderView(
    chapter: MangaChapter,
    onClose: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { chapter.pages.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            AsyncImage(
                model = chapter.pages[pageIndex],
                contentDescription = "صفحة ${pageIndex + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Overlay Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.spaceLg)
                .align(Alignment.TopCenter)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(Dimensions.radiusSm))
                .padding(horizontal = Dimensions.spaceMd, vertical = Dimensions.spaceSm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(Dimensions.iconButtonSize)
            ) {
                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
            }
            Text(
                text = "الفصل ${chapter.number}: ${chapter.title}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${pagerState.currentPage + 1} / ${chapter.pages.size}",
                color = CyberBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
