package com.sarathi.exoplayermanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import org.project.exocompose.SimpleController

@Composable
fun ExoMediaPlayer(url: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var url by rememberSaveable { mutableStateOf(url) }
        val player by rememberManagedExoPlayer()
        var setPlayer by rememberSaveable { mutableStateOf(true) }
        val mediaItem = remember(url) { MediaItem.Builder().setMediaId(url).setUri(url).build() }
        DisposableEffect(mediaItem, player) {

        player?.run {
                setMediaItem(mediaItem)
                prepare()
            }
            onDispose {}
        }
        val mediaState = rememberMediaState(player = player.takeIf { setPlayer })
        Media(
            state = mediaState,
            modifier = Modifier
                .aspectRatio(16f / 9f)
                .background(Color.Black),
        ) {
            SimpleController(
                mediaState = mediaState,
            )
        }
    }
}