package com.real.vibechat.presentation.story

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {


            when(state.content) {
                StoryType.IMAGE -> {
                    StoryImagePlayer(imageUrl, storyViewModel)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp),
                progress = progress
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(25.dp),
                    strokeWidth = 2.dp
                )
            }

        }

    }

}

@Composable
fun StoryImagePlayer(
    imageUrl: String?,
    storyViewModel: StoryViewModel
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "User profile Image",
        modifier = Modifier.fillMaxSize()
    ) {
        when (painter.state) {

            is AsyncImagePainter.State.Loading -> {
                // 👇 Show loading UI
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AsyncImagePainter.State.Success -> {
                // 👇 Start timer only after image fully loads
                storyViewModel.startImageTimer(4000)
                SubcomposeAsyncImageContent()
            }

            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(R.drawable.image_placeholder_icon),
                    contentDescription = "Error image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {}
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


