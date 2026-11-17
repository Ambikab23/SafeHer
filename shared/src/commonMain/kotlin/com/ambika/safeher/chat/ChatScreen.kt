
package com.ambika.safeher.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

private val ChatPink = Color(0xFFB81D52)
private val ChatBackground = Color(0xFFFDF7F9)
private val ChatText = Color(0xFF30252A)
private val ChatMuted = Color(0xFF88777E)
private val ChatBorder = Color(0xFFEADCE2)

@Composable
fun ChatScreen(
    repository: ChatRepository,
    onBack: () -> Unit
) {
    val chatViewModelFactory = remember(repository) {
        viewModelFactory {
            initializer {
                ChatViewModel(repository)
            }
        }
    }

    val chatViewModel: ChatViewModel = viewModel(
        factory = chatViewModelFactory
    )

    val messages by chatViewModel.messages.collectAsState()

    var messageText by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBackground)
            .safeDrawingPadding()
            .imePadding()
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = ChatPink
                ),
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 8.dp
                )
            ) {
                Text(
                    "‹ Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Emergency Chat",
                    color = ChatText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    "SafeHer Safety Communication",
                    color = ChatMuted,
                    fontSize = 12.sp
                )
            }
        }

        HorizontalDivider(
            color = ChatBorder,
            thickness = 1.dp
        )

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Your conversation starts here",
                                color = ChatText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                "Send a message to begin.",
                                color = ChatMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            items(
                items = messages,
                key = { it.id }
            ) { message ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isSentByMe) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 290.dp)
                            .background(
                                color = if (message.isSentByMe) {
                                    ChatPink
                                } else {
                                    Color.White
                                },
                                shape = RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomStart = if (message.isSentByMe) {
                                        18.dp
                                    } else {
                                        5.dp
                                    },
                                    bottomEnd = if (message.isSentByMe) {
                                        5.dp
                                    } else {
                                        18.dp
                                    }
                                )
                            )
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            )
                    ) {
                        Text(
                            text = message.message,
                            color = if (message.isSentByMe) {
                                Color.White
                            } else {
                                ChatText
                            },
                            fontSize = 15.sp,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = message.timestamp,
                            color = if (message.isSentByMe) {
                                Color.White.copy(alpha = 0.75f)
                            } else {
                                ChatMuted
                            },
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Message input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = {
                        messageText = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Type your message...",
                            color = ChatMuted,
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (messageText.isNotBlank()) {
                                chatViewModel.sendMessage(
                                    messageText.trim()
                                )
                                messageText = ""
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ChatPink,
                        unfocusedBorderColor = ChatBorder,
                        cursorColor = ChatPink,
                        focusedTextColor = ChatText,
                        unfocusedTextColor = ChatText
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            chatViewModel.sendMessage(
                                messageText.trim()
                            )
                            messageText = ""
                        }
                    },
                    enabled = messageText.isNotBlank(),
                    modifier = Modifier.height(48.dp),
                    contentPadding = PaddingValues(
                        horizontal = 17.dp
                    ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChatPink,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE8C5D1),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        "Send",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
