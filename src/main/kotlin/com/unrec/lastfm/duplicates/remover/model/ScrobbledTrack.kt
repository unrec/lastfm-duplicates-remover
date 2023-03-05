package com.unrec.lastfm.duplicates.remover.model

import com.unrec.lastfm.duplicates.remover.utils.artist
import com.unrec.lastfm.duplicates.remover.utils.timestamp
import com.unrec.lastfm.duplicates.remover.utils.track
import org.openqa.selenium.WebElement

data class ScrobbledTrack(
    val artist: String,
    val title: String,
    val timestamp: Long,
)

fun WebElement.toScrobbledTrack(): ScrobbledTrack {
    return ScrobbledTrack(
        artist = this.artist(),
        title = this.track(),
        timestamp = this.timestamp(),
    )
}