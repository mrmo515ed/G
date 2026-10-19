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
import com.animeblack.app.ui.components.GlassCard
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
                name = "صندوق الحظ الأسطوري",
                description = "يحتوي على بطاقات نادرة ولقب ملكي مضمون",
                type = "box",
                priceCoins = 300,
                rarity = "legendary",
                icon = "🎁"
            ),
            StoreItem(
                id = "card_sukuna",
                name = "بطاقة سوكونا الخاصة",
                description = "تزيد السمعة اليومية وتمنح هالة زرقاء ملكية للملف الشخصي",
                type = "card",
                priceCoins = 150,
                rarity = "epic",
                icon = "🃏"
            ),
            StoreItem(
                id = "frame_fire",
                name = "إطار السايبر المتوهج",
                description = "إطار مشع يزين صورة حسابك في كل التعليقات والمحادثات",
                type = "frame",
                priceCoins = 200,
                rarity = "rare",
                icon = "⚡"
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
                name = "حزمة 50 نجمة زرقاء",
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
            .background(AmoledBlack)
            .padding(Dimensions.spaceLg)
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
            Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSm)) {
                Surface(
                    shape = RoundedCornerShape(Dimensions.radiusFull),
                    color = DarkCharcoal,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Text(
                        text = "🪙 $currentCoins",
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = Dimensions.spaceMd, vertical = Dimensions.spaceXs)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(Dimensions.radiusFull),
                    color = DarkCharcoal,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Text(
                        text = "⭐ $userStars",
                        color = ElectricPurpleLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = Dimensions.spaceMd, vertical = Dimensions.spaceXs)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.spaceLg))

        // Grid of Store Items
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceMd),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spaceMd),
            modifier = Modifier.weight(1f)
        ) {
            items(items) { item ->
                GlassCard(
                    cornerRadius = Dimensions.radiusLg,
                    borderAlpha = 0.15f,
                    onClick = { selectedItemForBuy = item }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(Dimensions.radiusMd))
                                .background(
                                    when (item.rarity) {
                                        "legendary" -> CyberBlue.copy(alpha = 0.25f)
                                        "epic" -> ElectricPurple.copy(alpha = 0.25f)
                                        else -> StatusSuccess.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.icon, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(Dimensions.spaceSm))
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
                        Spacer(modifier = Modifier.height(Dimensions.spaceMd))
                        Button(
                            onClick = { selectedItemForBuy = item },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                            shape = RoundedCornerShape(Dimensions.radiusSm),
                            contentPadding = PaddingValues(horizontal = Dimensions.spaceMd, vertical = Dimensions.spaceXs),
                            modifier = Modifier.heightIn(min = 38.dp)
                        ) {
                            Text("🪙 ${item.priceCoins}", color = AmoledBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            containerColor = DarkCharcoal,
            title = { Text(target.name, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "هل تود شراء ${target.name} مقابل ${target.priceCoins} عملة؟ سيتم إضافة العنصر فوراً إلى حقيبة مقتنياتك.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentCoins >= target.priceCoins) {
                            currentCoins -= target.priceCoins
                            onBuyItem(target)
                        }
                        selectedItemForBuy = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                ) {
                    Text("تأكيد الشراء", color = AmoledBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForBuy = null }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }
}
