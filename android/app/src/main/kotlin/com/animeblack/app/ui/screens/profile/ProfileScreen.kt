package com.animeblack.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.model.User
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.theme.*

@Composable
fun ProfileScreen(
    firebaseRepository: FirebaseRepository
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
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // User Card Header
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AccentRed, AccentGold))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = displayUser.name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${displayUser.username} · ${displayUser.role}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Level Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المستوى ${displayUser.level}",
                                color = AccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "XP 750 / 1000",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AccentGold,
                            trackColor = DarkSurface
                        )
                    }
                }
            }
        }

        // Wallet & Economy Badges
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Coins Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("العملات", color = TextSecondary, fontSize = 11.sp)
                            Text("${displayUser.coins}", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                // Stars Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("النجوم", color = TextSecondary, fontSize = 11.sp)
                            Text("${displayUser.stars}", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Badges Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "الأوسمة والإنجازات",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("🏆", "صياد النخبة", "أكمل 50 حلقة"),
                    Triple("🔥", "عاشق الشونين", "شاهد 10 أنميات"),
                    Triple("⚡", "المتفوق", "نشط لمدة 30 يوم")
                ).forEach { (icon, title, desc) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(icon, fontSize = 26.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(desc, color = TextMuted, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }
            }
        }

        // Settings / Account Options
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "إعدادات الحساب والتطبيق",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(listOf(
            Pair("سجل المشاهدات والمفضلة", Icons.Default.History),
            Pair("إدارة الإشعارات وتنبيهات الحلقات", Icons.Default.Notifications),
            Pair("الأجهزة والجلسات النشطة", Icons.Default.Devices),
            Pair("سياسة الخصوصية وشروط الاستخدام", Icons.Default.Security)
        )) { (label, icon) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
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
