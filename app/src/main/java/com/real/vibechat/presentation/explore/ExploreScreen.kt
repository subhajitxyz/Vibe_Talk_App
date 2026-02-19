package com.real.vibechat.presentation.explore

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.real.vibechat.R
import com.real.vibechat.domain.models.User
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun ExploreScreen(
    navController: NavController,
    exploreViewModel: ExploreViewModel = hiltViewModel()
) {

    val exploreUiState by exploreViewModel.exploreUiState.collectAsStateWithLifecycle()

    when(exploreUiState) {
        ExploreUiState.Loading -> LoadingScreen()
        is ExploreUiState.Success -> {
            ExploreUserListScreen(
                (exploreUiState as ExploreUiState.Success).users,
                exploreViewModel,
                navController
            )
        }
        is ExploreUiState.Error -> ErrorScreen(
            (exploreUiState as ExploreUiState.Error).e,
            exploreViewModel
        )
    }

}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(25.dp),
            strokeWidth = 2.dp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(
    e: String,
    exploreViewModel: ExploreViewModel
) {

    val isRefreshing = exploreViewModel.isRefreshing
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {exploreViewModel.refreshUsers()},
        state = pullRefreshState,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = e)
            Text(text = "Pull down to try again")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreUserListScreen(
    users: List<User>,
    exploreViewModel: ExploreViewModel,
    navController: NavController
) {

    val isRefreshing = exploreViewModel.isRefreshing
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {exploreViewModel.refreshUsers()},
        state = pullRefreshState,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            val cellConfiguration = if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) {
                StaggeredGridCells.Adaptive(minSize = 150.dp)
            } else StaggeredGridCells.Fixed(3)

            Text(
                text = "Connects with new peoples",
                textAlign = TextAlign.Start,
                fontSize = 28.sp,
                color = if(isSystemInDarkTheme()) Color.White else PrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 25.dp, top = 15.dp, end = 70.dp, bottom = 10.dp)
            )

            LazyVerticalStaggeredGrid(
                columns = cellConfiguration,
                modifier = Modifier.fillMaxSize(),
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(users) { user ->
                    UserCard(user) { imageUrl, videoUrl ->
                       navController.navigate(
                                AppScreen.Story.createRoute(imageUrl, videoUrl)
                            )

                    }
                }
            }
        }


    }

}

@Composable
fun UserCard(
    user: User,
    onProfileClick: (String?,String?) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height((150..400).random().dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .aspectRatio(1f)// slightly bigger than image
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    color = if(user.profileIntroVideo!= null) Color.Green
                    else Color.Gray,
                    shape = CircleShape
                )
                .padding(2.dp) // space between ring & image
                .clickable(
                    onClick = { onProfileClick(user.profileImage, user.profileIntroVideo) }
                ),
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.image_placeholder_icon),
                error = painterResource(R.drawable.image_placeholder_icon)
            )
        }


        Text(
            text = user.username,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
