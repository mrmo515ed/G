package com.animeblack.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.ui.theme.*

// Glassmorphism Card matching website's dark surfaces
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = Dimensions.radiusLg,
    borderAlpha: Float = 0.12f,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = DarkCharcoal,
        border = BorderStroke(1.dp, Color.White.copy(alpha = borderAlpha)),
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.spaceLg),
            content = content
        )
    }
}

// Official Cyber Blue / Purple Gradient Button
@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = GradCyberBlue,
    icon: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .heightIn(min = Dimensions.minTouchTarget)
            .clip(RoundedCornerShape(Dimensions.radiusMd))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(horizontal = Dimensions.spaceXl, vertical = Dimensions.spaceMd),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(Dimensions.spaceSm))
            }
            Text(
                text = text,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// Otaku Badge matching website's badge with glowing accents
@Composable
fun OtakuBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CyberBlue,
    backgroundColor: Color = color.copy(alpha = 0.15f)
) {
    Surface(
        shape = RoundedCornerShape(Dimensions.radiusSm),
        color = backgroundColor,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = Dimensions.spaceSm, vertical = Dimensions.space2xs)
        )
    }
}
