package com.animeblack.app.ui.screens.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.model.StoreItem
import com.animeblack.app.ui.theme.*

@Composable
fun StoreScreen(
    userCoins: Long = 450,
    userStars: Long = 25,
    onBuyItem: (StoreItem) -> Unit = {}
) {
    val items = remember {
        listOf(
            StoreItem(
                id = "box_gold",
                name = "صندوق الحظ الذهبي",
                description = "يحتوي على بطاقات نادرة ولقب أسطوري مضمون",
                type = "box",
                priceCoins = 300,
                rarity = "legendary",
                icon = "🎁"
            ),
            StoreItem(
                id = "card_sukuna",
                name = "بطاقة سوكونا الخاصة",
                description = "تزيد السمعة اليومية وتمنح هالة حمراء للملف الشخصي",
                type = "card",
                priceCoins = 150,
                rarity = "epic",
                icon = "🃏"
            ),
            StoreItem(
                id = "frame_fire",
                name = "إطار اللهب المتوهج",
                description = "إطار ناري متحرك يزين صورة حسابك في كل التعليقات",
                type = "frame",
                priceCoins = 200,
                rarity = "rare",
                icon = "🔥"
            ),
            StoreItem(
                id = "title_emperor",
                name = "لقب: إمبراطور الأوتاكو",
                description = "لقب شرفي يعرض بجانب اسمك في غرف الدردشة",
                type = "title",
                priceCoins = 500,
                rarity = "legendary",
                icon = "👑"
            ),
            StoreItem(
                id = "box_silver",
                name = "صندوق الحظ الفضي",
                description = "يحتوي على عملات إضافية ونقاط خبرة XP",
                type = "box",
                priceCoins = 100,
                rarity = "rare",
                icon = "📦"
            ),
            StoreItem(
                id = "stars_pack",
                name = "حزمة 50 نجمة ذهبية",
                description = "استخدم النجوم لترقية حسابك والحصول على امتيازات خاصة",
                type = "currency",
                priceCoins = 400,
                priceStars = 50,
                rarity = "epic",
                icon = "⭐"
            )
        )
    }

    var selectedItemForBuy by remember { mutableStateOf<StoreItem?>(null) }
    var currentCoins by remember { mutableLongStateOf(userCoins) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // Top Header with Balances
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "متجر ومزاد الأوتاكو",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard
                ) {
                    Text(
                        text = "🪙 $currentCoins",
                        color = AccentGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard
                ) {
                    Text(
                        text = "⭐ $userStars",
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of Store Items
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(items) { item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    modifier = Modifier.clickable { selectedItemForBuy = item }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when (item.rarity) {
                                        "legendary" -> AccentGold.copy(alpha = 0.2f)
                                        "epic" -> AccentRed.copy(alpha = 0.2f)
                                        else -> AccentCyan.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.icon, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = item.description,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 2,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { selectedItemForBuy = item },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("🪙 ${item.priceCoins}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Purchase confirmation dialog
    if (selectedItemForBuy != null) {
        val target = selectedItemForBuy!!
        AlertDialog(
            onDismissRequest = { selectedItemForBuy = null },
            title = { Text(target.name) },
            text = {
                Text(
                    "هل تود شراء ${target.name} مقابل ${target.priceCoins} عملة؟ سيتم إضافة العنصر فوراً إلى حقيبة مقتنياتك."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (currentCoins >= target.priceCoins) {
                            currentCoins -= target.priceCoins
                            onBuyItem(target)
                        }
                        selectedItemForBuy = null
                    }
                ) {
                    Text("تأكيد الشراء", color = AccentGold, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForBuy = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
