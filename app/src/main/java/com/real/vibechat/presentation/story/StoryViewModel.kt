package com.real.vibechat.presentation.story

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _state = MutableStateFlow(StoryUiState())
    val state: StateFlow<StoryUiState> = _state

    var exoPlayer: ExoPlayer? = null
    var progressJob: Job? = null

    var imageUrl: String? = null
    var videoUrl: String? = null
    private var playbackPosition: Long = 0L

    var hasVideo = false

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private fun startImageProgressTracking() {

        progressJob = viewModelScope.launch {
            _progress.value = 0f
            val startTime = System.currentTimeMillis()
            val duration: Long = 4000
            while (_progress.value < 1f) {

                val elapsed = System.currentTimeMillis() - startTime
                _progress.value = (elapsed.toFloat() / duration).coerceAtMost(1f)

                delay(16) // ~60fps smooth animation
            }
        }
    }

    private fun startVideoProgressTracking() {
        progressJob = viewModelScope.launch {
            _progress.value = 0f
            while (true) {
                exoPlayer?.let {
                    val duration = it.duration
                    val position = it.currentPosition

                    if (duration > 0) {
                        _progress.value = position.toFloat() / duration.toFloat()
                    }
                    delay(50) // smooth animation (20fps)
                }
            }
        }
    }

    fun initialize(imageUrl: String?, videoUrl: String?) {
        if(exoPlayer != null) return
        this.imageUrl = imageUrl
        this.videoUrl = videoUrl
        hasVideo = (videoUrl != null && videoUrl.isNotEmpty())
        // if it has video url, prepare the player.
        videoUrl?.let { url ->
            exoPlayer = ExoPlayer.Builder(context).build() .apply {
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                prepare()
                seekTo(playbackPosition)

                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when(state) {
                            Player.STATE_ENDED -> closeStoryScreen()
                            Player.STATE_BUFFERING -> _state.update { it.copy(isLoading = true) }
                            Player.STATE_READY -> {
                                _state.update { it.copy(isLoading = false) }
                            }
                        }
                    }
                })

            }
        }

    }

    fun startImageTimer(time: Long) {
        startImageProgressTracking()
        viewModelScope.launch {
            delay(time)

            // after delay switch to video.
            if (hasVideo) {
                _state.update { it.copy(content = StoryType.VIDEO) }
                // play video
                playVideo()

            } else {
                closeStoryScreen()
            }
        }
    }

    fun closeStoryScreen() {
        progressJob = null
        _state.update { it.copy(isFinished = true) }
    }

    fun savePlayerState() {
        exoPlayer?.let {
            playbackPosition = it.currentPosition
        }
    }

    override fun onCleared() {
        exoPlayer?.release()
        exoPlayer = null
        super.onCleared()
    }

    @OptIn(UnstableApi::class)
    private fun playVideo() {
        exoPlayer?.play()
        startVideoProgressTracking()
    }

    fun pausePlayer() {
        exoPlayer?.pause()
    }

    fun resumePlayer() {
        exoPlayer?.play()
    }
}