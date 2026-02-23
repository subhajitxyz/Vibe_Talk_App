package com.real.vibechat.presentation.chats

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.real.vibechat.R
import com.real.vibechat.domain.models.ChatRoom
import com.real.vibechat.domain.models.toFormattedTime
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun ChatsScreen(
    navController: NavController,
    viewModel: ChatsViewModel = hiltViewModel()
) {

    val chatRoomsList = viewModel.chatRoomsList.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Vibe Chat",
                fontSize = 32.sp,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )
            Spacer(Modifier.height(4.dp))
            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(chatRoomsList.value) { item ->
                UserChatBlock(item, navController = navController)
            }
        }

    }

}


@Composable
fun UserChatBlock(chatroom: ChatRoom, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        CircularProfile(chatroom.friendProfileUrl, navController)

        Row(
            modifier = Modifier.clickable(
                onClick = {
                    navController.navigate(AppScreen.ChatRoomScreen.createRoute(chatroom.friendId))
                }
            )
        ) {
            NameAndLastMessageBlock(Modifier.weight(1f), chatroom.friendName, chatroom.lastMessage)
            LastMessageTimeBlock(chatroom.sentAt)
        }
    }
}

@Composable
fun LastMessageTimeBlock(sentAt: Long?) {
    Text(
        modifier = Modifier.padding(1.dp),
        fontSize = 12.sp,
        text = sentAt?.toFormattedTime()?: "",
        color = Color.Gray
    )
}

@Composable
fun NameAndLastMessageBlock(modifier: Modifier, friendName: String, lastMessage: String) {
    Column(
        modifier = modifier.padding(start = 10.dp, end = 6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = friendName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))

        Text(
            text = lastMessage,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )

    }
}



@Composable
fun CircularProfile(friendProfileUrl: String?, navController: NavController) {
    AsyncImage(
        modifier = Modifier
            .size(50.dp)
            .border(
                2.dp,
                color = Color.Green,
                shape = CircleShape
            )
            .clickable(
                onClick = {
                    navController.navigate(AppScreen.Story.createRoute(friendProfileUrl, null))
                }
            )
            .padding(4.dp)
            .clip(CircleShape),
        model = friendProfileUrl,
        contentDescription = "",
        placeholder = painterResource(R.drawable.image_placeholder_icon)
    )
}


