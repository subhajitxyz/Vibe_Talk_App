package com.real.vibechat.presentation.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.real.vibechat.R

@Composable
fun StoryScreen(
    imageUrl: String?,
    videoUrl: String?,
    rootNavController: NavController,
    storyViewModel: StoryViewModel = hiltViewModel()
) {

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        val state by storyViewModel.state.collectAsStateWithLifecycle()
        val progress by storyViewModel.progress.collectAsStateWithLifecycle()

        LaunchedEffect(storyViewModel.videoUrl) {
            storyViewModel.initialize(imageUrl, videoUrl)
        }

        DisposableEffect(Unit) {
            onDispose {
                storyViewModel.savePlayerState()
            }
        }
        LaunchedEffect(state.isFinished) {
            if(state.isFinished) {
                rootNavController.popBackStack()
            }
        }

        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        storyViewModel.pausePlayer()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        storyViewModel.resumePlayer()
                    }
                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {


            when(state.content) {
                StoryType.IMAGE -> {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = imageUrl,
                        contentDescription = "User profile Image",
                        placeholder = painterResource(R.drawable.image_placeholder_icon),
                        error = painterResource(id = R.drawable.image_placeholder_icon),
                        onSuccess = {
                            // start timer for showing video after 4 sec.
                            storyViewModel.startImageTimer(4000)

                        }
                    )
                }

                StoryType.VIDEO -> {
                    storyViewModel.videoUrl?.let {
                        storyViewModel.exoPlayer?.let { player ->
                            StoryVideoPlayer(player)
                        }

                    }
                }

            }

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.TopCenter).padding(top = 10.dp),
                progress = progress
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).size(25.dp),
                    strokeWidth = 2.dp
                )
            }

        }

    }

}

@Composable
fun StoryVideoPlayer(
    exoPlayer: ExoPlayer
) {


    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}


