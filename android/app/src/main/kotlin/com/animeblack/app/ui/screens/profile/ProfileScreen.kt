package com.animeblack.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.model.User
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.components.OtakuBadge
import com.animeblack.app.ui.theme.*

@Composable
fun ProfileScreen(
    firebaseRepository: FirebaseRepository,
    onSettingsClick: () -> Unit = {},
    onLibraryClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val uid = firebaseRepository.currentUserId ?: "guest"
    var userProfile by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(uid) {
        if (uid != "guest") {
            firebaseRepository.observeUserProfile(uid).collect { u ->
                userProfile = u
            }
        }
    }

    val displayUser = userProfile ?: User(
        name = "أوتاكو أنمي بلاك",
        username = "otaku_master",
        email = firebaseRepository.currentUserEmail ?: "otaku@animeblack.com",
        level = 5,
        coins = 450,
        stars = 25,
        reputation = 85
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(Dimensions.spaceLg)
    ) {
        // User Card Header with Glassmorphism
        item {
            GlassCard(
                cornerRadius = Dimensions.radiusXl,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimensions.avatarLg)
                            .clip(CircleShape)
                            .background(GradCyberBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.height(Dimensions.spaceMd))
                    Text(
                        text = displayUser.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Dimensions.space2xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "@${displayUser.username}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                        OtakuBadge(text = displayUser.role, color = CyberBlue)
                    }
                    Spacer(modifier = Modifier.height(Dimensions.spaceLg))

                    // Level Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المستوى ${displayUser.level}",
                                color = CyberBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "XP 750 / 1000",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyberBlue,
                            trackColor = DarkSlate
                        )
                    }
                }
            }
        }

        // Wallet & Economy Badges
        item {
            Spacer(modifier = Modifier.height(Dimensions.spaceMd))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceMd)
            ) {
                // Coins Card
                GlassCard(
                    cornerRadius = Dimensions.radiusMd,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                        Column {
                            Text("العملات", color = TextSecondary, fontSize = 11.sp)
                            Text("${displayUser.coins}", color = CyberBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                // Stars Card
                GlassCard(
                    cornerRadius = Dimensions.radiusMd,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                        Column {
                            Text("النجوم", color = TextSecondary, fontSize = 11.sp)
                            Text("${displayUser.stars}", color = ElectricPurpleLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Badges Section
        item {
            Spacer(modifier = Modifier.height(Dimensions.spaceXl))
            Text(
                text = "الأوسمة والإنجازات",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceSm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSm)
            ) {
                listOf(
                    Triple("🏆", "صياد النخبة", "أكمل 50 حلقة"),
                    Triple("🔥", "عاشق الشونين", "شاهد 10 أنميات"),
                    Triple("⚡", "المتفوق", "نشط لمدة 30 يوم")
                ).forEach { (icon, title, desc) ->
                    GlassCard(
                        cornerRadius = Dimensions.radiusMd,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(Dimensions.spaceXs))
                            Text(title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(desc, color = TextMuted, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }
            }
        }

        // Settings / Account Options
        item {
            Spacer(modifier = Modifier.height(Dimensions.spaceXl))
            Text(
                text = "إعدادات الحساب والتطبيق",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceSm))
        }

        items(listOf(
            Triple("سجل المشاهدات والمفضلة", Icons.Default.History, onLibraryClick),
            Triple("إدارة الإشعارات وتنبيهات الحلقات", Icons.Default.Notifications, onNotificationsClick),
            Triple("إعدادات التطبيق والحساب", Icons.Default.Settings, onSettingsClick),
            Triple("الأجهزة والجلسات النشطة", Icons.Default.Devices, onSettingsClick)
        )) { (label, icon, onClickAction) ->
            GlassCard(
                cornerRadius = Dimensions.radiusMd,
                onClick = onClickAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.spaceXs)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = Dimensions.minTouchTarget),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                    Text(label, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextMuted)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
