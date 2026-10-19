package com.animeblack.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.theme.*

@Composable
fun SettingsScreen(
    firebaseRepository: FirebaseRepository,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    var selectedQuality by remember { mutableStateOf("1080p FHD") }
    var wifiOnlyDownload by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoPlayNext by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val isUserLoggedIn = firebaseRepository.isUserSignedIn()

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
                text = "الإعدادات العامة",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(start = Dimensions.spaceSm)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimensions.spaceLg),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spaceLg)
        ) {
            // Video Playback Section
            Text(
                text = "مشغل الفيديو والبث",
                color = CyberBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            GlassCard {
                SettingsDropdownRow(
                    title = "جودة الفيديو الافتراضية",
                    subtitle = "حدد دقة البث التلقائية للمشاهدة",
                    currentValue = selectedQuality,
                    options = listOf("1080p FHD", "720p HD", "480p SD", "تلقائي Auto"),
                    onSelect = { selectedQuality = it },
                    icon = Icons.Default.HighQuality
                )

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = Dimensions.spaceMd))

                SettingsSwitchRow(
                    title = "التشغيل التلقائي للحلقة التالية",
                    subtitle = "الانتقال المباشر للحلقة التالية عند نهاية الحلقة",
                    checked = autoPlayNext,
                    onCheckedChange = { autoPlayNext = it },
                    icon = Icons.Default.SkipNext
                )

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = Dimensions.spaceMd))

                SettingsSwitchRow(
                    title = "التنزيل عبر Wi-Fi فقط",
                    subtitle = "منع استهلاك باقة البيانات المتنقلة أثناء كاش الحلقات",
                    checked = wifiOnlyDownload,
                    onCheckedChange = { wifiOnlyDownload = it },
                    icon = Icons.Default.Wifi
                )
            }

            // Notifications Section
            Text(
                text = "التنبيهات والإشعارات",
                color = CyberBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            GlassCard {
                SettingsSwitchRow(
                    title = "إشعارات الحلقات الجديدة",
                    subtitle = "تلقي تنبيهات عند نزول حلقات الأنميات المتابعة",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    icon = Icons.Default.NotificationsActive
                )
            }

            // Account & Session Section
            Text(
                text = "الحساب والجلسة",
                color = CyberBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            GlassCard {
                if (isUserLoggedIn) {
                    SettingsActionRow(
                        title = "تسجيل الخروج",
                        subtitle = "الخروج من الحساب الحالي على هذا الجهاز",
                        onClick = { showLogoutDialog = true },
                        icon = Icons.Default.Logout,
                        tint = StatusError
                    )
                } else {
                    SettingsActionRow(
                        title = "تسجيل الدخول / إنشاء حساب",
                        subtitle = "مزامنة نقاطك والأنميات المفضلة مع السيرفر السحابي",
                        onClick = onNavigateToLogin,
                        icon = Icons.Default.Login,
                        tint = CyberBlue
                    )
                }
            }

            // About App Section
            Text(
                text = "حول التطبيق",
                color = CyberBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                    Column {
                        Text("أنمي بلاك — Anime Black Android", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("الإصدار 1.0.0 (Native Jetpack Compose)", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تسجيل الخروج", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج من تطبيق أنمي بلاك؟", color = TextSecondary) },
            containerColor = DarkCharcoal,
            confirmButton = {
                TextButton(
                    onClick = {
                        firebaseRepository.signOut()
                        showLogoutDialog = false
                    }
                ) {
                    Text("نعم، خروج", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("إلغاء", color = TextPrimary)
                }
            }
        )
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(Dimensions.spaceMd))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AmoledBlack,
                checkedTrackColor = CyberBlue,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSlate
            )
        )
    }
}

@Composable
fun SettingsDropdownRow(
    title: String,
    subtitle: String,
    currentValue: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    icon: ImageVector
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(Dimensions.spaceMd))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Box {
            Text(currentValue, color = CyberBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(DarkCharcoal)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextPrimary) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(Dimensions.spaceMd))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = tint, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextMuted)
    }
}
