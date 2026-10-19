package com.animeblack.app.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.model.NotificationItem
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.components.EmptyStateView
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    firebaseRepository: FirebaseRepository,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val uid = firebaseRepository.currentUserId ?: "guest"
    val liveNotifs by firebaseRepository.observeNotifications(uid).collectAsState(initial = emptyList())

    // Mock initial notification samples if database is empty
    val defaultNotifs = remember {
        listOf(
            NotificationItem(
                id = "n1",
                title = "حلقة جديدة متوفرة!",
                body = "تمت إضافة الحلقة 24 من جوجوتسو كايسن بجودة 1080p FHD.",
                type = "episode",
                isRead = false,
                createdAt = System.currentTimeMillis() - 3600000
            ),
            NotificationItem(
                id = "n2",
                title = "مكافأة تسجيل الدخول اليومي",
                body = "حصلت على 50 عملة أوتاكو و10 نقاط خبرة XP!",
                type = "reward",
                isRead = true,
                createdAt = System.currentTimeMillis() - 7200000
            ),
            NotificationItem(
                id = "n3",
                title = "تحديث مجتمع أنمي بلاك",
                body = "علّق أحد الأعضاء على منشورك الأخير في منتدى الأوتاكو.",
                type = "community",
                isRead = true,
                createdAt = System.currentTimeMillis() - 86400000
            )
        )
    }

    val displayNotifs = if (liveNotifs.isNotEmpty()) liveNotifs else defaultNotifs

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
                text = "الإشعارات والتنبيهات",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = Dimensions.spaceSm)
            )
        }

        if (displayNotifs.isEmpty()) {
            EmptyStateView(
                title = "لا توجد إشعارات جديدة",
                description = "ستظهر هنا تنبيهات الحلقات الجديدة والردود ومكافآت الأوتاكو اليومية",
                icon = Icons.Default.NotificationsNone
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(Dimensions.spaceLg),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spaceMd)
            ) {
                items(displayNotifs) { notif ->
                    GlassCard(
                        borderAlpha = if (notif.isRead) 0.08f else 0.35f,
                        onClick = {
                            coroutineScope.launch {
                                firebaseRepository.markNotificationRead(notif.id)
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "episode" -> CyberBlueMuted
                                            "reward" -> StatusWarning.copy(alpha = 0.2f)
                                            else -> ElectricPurpleMuted
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "episode" -> Icons.Default.PlayArrow
                                        "reward" -> Icons.Default.CardGiftcard
                                        else -> Icons.Default.Forum
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "episode" -> CyberBlue
                                        "reward" -> StatusWarning
                                        else -> ElectricPurpleLight
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(Dimensions.spaceMd))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(CyberBlue)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(Dimensions.spaceXs))

                                Text(
                                    text = notif.body,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
