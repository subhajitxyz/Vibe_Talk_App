package com.real.vibechat.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.real.vibechat.R
import com.real.vibechat.domain.models.Message
import com.real.vibechat.domain.models.UserProfile
import com.real.vibechat.domain.models.toFormattedTime
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun ChatRoomScreen(
    chatRoomViewModel: ChatRoomViewModel = hiltViewModel(),
    navController: NavController
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    val chatState by chatRoomViewModel.state.collectAsStateWithLifecycle()
    var inputMessage by rememberSaveable { mutableStateOf("") }

    val listState = rememberLazyListState()
    LaunchedEffect(chatState.messages.size) {
        listState.animateScrollToItem(0)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    chatRoomViewModel.setActiveChatRoom()
                }

                Lifecycle.Event.ON_STOP -> {
                    chatRoomViewModel.removeActiveChatRoom()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            chatRoomViewModel.removeActiveChatRoom()
        }
    }



    Scaffold(
        topBar = {
            ChatHeader(
                userProfile = chatState.userProfile,
                onProfileClick = { imageUrl, videoUrl ->
                    navController.navigate(
                        AppScreen.Story.createRoute(imageUrl, videoUrl)
                    )

                },
                onBackClick = {
                    navController.popBackStack()
                },
                onMenuClick = {  }
            )
        },
        bottomBar =  {
            ChatInputField(
                message = inputMessage,
                onMessageChange = { it ->
                    inputMessage = it
                },
                onSendClick = {
                    chatRoomViewModel.onIntent(ChatIntent.SendMessage(inputMessage))
                    inputMessage = ""
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                reverseLayout = true
            ) {
                items(
                    chatState.messages,
                    key = { message -> message.id}
                ) { item ->
                    when(item.senderType) {
                        SenderType.SELF ->  SelfBubble(item)
                        SenderType.OTHER -> OtherBubble(item)
                    }
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(
    userProfile: UserProfile,
    onProfileClick: (String?, String?)-> Unit,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Profile image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.image_placeholder_icon),
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = { onProfileClick(userProfile.imageUrl, userProfile.videoUrl) }
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(userProfile.name)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    )
}




@Composable
fun SelfBubble(
    message: Message
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.End
    ) {

        Surface(
            modifier = Modifier.padding(start = 70.dp, end = 10.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 0.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if(isSystemInDarkTheme()) Color(0xFF5E82C2)
                else Color(0xFFC6DEF8)
        ) {

            Column(
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 4.dp
                    )
            ) {

                // Message
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(1.dp))

                // Time + Tick (NO fillMaxWidth)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = message.sentAt?.toFormattedTime() ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color =  if (isSystemInDarkTheme()) Color.LightGray
                            else Color.Gray
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(
                            when(message.messageStatus) {
                                MessageStatus.SENT -> R.drawable.double_tick_icon
                                MessageStatus.PENDING -> R.drawable.single_tick_icon
                                MessageStatus.FAILED -> R.drawable.failed_message_icon
                            }
                        ),
                        contentDescription = null,
//                        tint = if (isSeen) Color(0xFF34B7F1) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun OtherBubble(
    message: Message
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        Surface(
            modifier = Modifier.padding(start = 10.dp, end = 70.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if(isSystemInDarkTheme()) Color(0xFFC2865E)
            else Color(0xFFF8E0C6)
        ) {

            Column(
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 4.dp
                    )
            ) {

                // Message
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(1.dp))

                // Time + Tick (NO fillMaxWidth)
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = message.sentAt?.toFormattedTime()?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color =  if (isSystemInDarkTheme()) Color.LightGray
                          else Color.Gray
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(
                            when(message.messageStatus) {
                                MessageStatus.SENT -> R.drawable.double_tick_icon
                                MessageStatus.PENDING -> R.drawable.single_tick_icon
                                MessageStatus.FAILED -> R.drawable.failed_message_icon
                            }
                        ),
                        contentDescription = null,
//                        tint = if (isSeen) Color(0xFF34B7F1) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun ChatInputField(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier= modifier,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Message Text Field Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                BasicTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    decorationBox = { innerTextField ->

                        if (message.isEmpty()) {
                            Text(
                                text = "Type a message",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }

                        innerTextField()
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Send Button
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onSendClick()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (message.isNotBlank())
                            PrimaryColor
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (message.isNotBlank())
                        Color.White
                    else
                        Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Previde(modifier: Modifier = Modifier) {
    ChatInputField(
        message = "fafa",
        onMessageChange = {

        },
        onSendClick = {

        },
        modifier = Modifier
    )
}
