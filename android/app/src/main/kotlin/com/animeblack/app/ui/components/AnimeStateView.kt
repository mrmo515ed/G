package com.animeblack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.ui.theme.*

// 1. Shimmer Loading Skeleton for Anime Grid
@Composable
fun AnimeGridSkeleton(
    modifier: Modifier = Modifier,
    itemsCount: Int = 4
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spaceLg),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceMd)
    ) {
        repeat(itemsCount) {
            Column(modifier = Modifier.width(Dimensions.animePosterWidth)) {
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.animePosterHeight),
                    cornerRadius = Dimensions.radiusLg
                )
                Spacer(modifier = Modifier.height(Dimensions.spaceSm))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(14.dp),
                    cornerRadius = Dimensions.radiusSm
                )
                Spacer(modifier = Modifier.height(Dimensions.spaceXs))
                ShimmerBox(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(11.dp),
                    cornerRadius = Dimensions.radiusSm
                )
            }
        }
    }
}

// 2. Error State View with Retry Button
@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.space2xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = StatusError,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceMd))
            Text(
                text = "تعذر تحميل البيانات",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceXs))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceLg))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(Dimensions.radiusMd),
                modifier = Modifier.height(Dimensions.buttonHeight)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = AmoledBlack)
                Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                Text("إعادة المحاولة", color = AmoledBlack, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 3. Empty State View
@Composable
fun EmptyStateView(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Inbox,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.space2xl),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceMd))
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceXs))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(Dimensions.spaceLg))
                OutlinedButton(
                    onClick = onAction,
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(CyberBlue, ElectricPurple))),
                    shape = RoundedCornerShape(Dimensions.radiusMd),
                    modifier = Modifier.height(Dimensions.buttonHeight)
                ) {
                    Text(actionText, color = CyberBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 4. Offline Indicator Banner
@Composable
fun OfflineIndicatorBanner(
    modifier: Modifier = Modifier
) {
    Surface(
        color = StatusWarning.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, StatusWarning.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(Dimensions.radiusSm),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceSm)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimensions.spaceMd, vertical = Dimensions.spaceSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = null,
                tint = StatusWarning,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Dimensions.spaceSm))
            Text(
                text = "وضع عدم الاتصال — يتم عرض المحتوى والأنميات المحفوظة في الكاش",
                color = StatusWarning,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
