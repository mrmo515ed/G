package com.animeblack.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.animeblack.app.data.model.ChatMessage
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ChatScreen(
    firebaseRepository: FirebaseRepository,
    groupId: String = "public_general"
) {
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    val currentUserId = firebaseRepository.currentUserId ?: "guest_user"

    LaunchedEffect(groupId) {
        firebaseRepository.observeGroupMessages(groupId).collect { list ->
            messages = list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        // Chat Top Bar
        Surface(
            color = DarkCharcoal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceMd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(GradCyberBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚔️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(Dimensions.spaceMd))
                Column {
                    Text(
                        text = "غرفة محادثة الأوتاكو العامة",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "متصلون الآن · نقاشات الأنمي والموسم الجديد",
                        color = StatusSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = Dimensions.spaceLg, vertical = Dimensions.spaceSm),
            reverseLayout = false
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUserId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimensions.spaceXs),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    if (!isMe) {
                        AsyncImage(
                            model = msg.senderAvatar,
                            contentDescription = msg.senderName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(Dimensions.avatarSm)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                    }

                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        if (!isMe) {
                            Text(
                                text = msg.senderName,
                                color = CyberBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = Dimensions.radiusMd,
                                topEnd = Dimensions.radiusMd,
                                bottomStart = if (isMe) Dimensions.radiusMd else Dimensions.space2xs,
                                bottomEnd = if (isMe) Dimensions.space2xs else Dimensions.radiusMd
                            ),
                            color = if (isMe) CyberBlue else DarkSlate,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (isMe) AmoledBlack else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isMe) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.padding(Dimensions.spaceMd)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Input Row
        Surface(
            color = DarkCharcoal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.spaceMd, vertical = Dimensions.spaceSm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("اكتب رسالتك هنا...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Dimensions.spaceSm),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CyberBlue,
                        unfocusedBorderColor = DarkBorder
                    ),
                    shape = RoundedCornerShape(Dimensions.radiusFull),
                    maxLines = 4
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val newMsg = ChatMessage(
                                id = "msg_" + UUID.randomUUID().toString(),
                                senderId = currentUserId,
                                senderName = "أوتاكو",
                                senderAvatar = "",
                                text = messageText.trim(),
                                timestamp = System.currentTimeMillis()
                            )
                            messageText = ""
                            coroutineScope.launch {
                                firebaseRepository.sendMessage(groupId, newMsg)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(Dimensions.buttonHeight)
                        .background(CyberBlue, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "إرسال",
                        tint = AmoledBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
