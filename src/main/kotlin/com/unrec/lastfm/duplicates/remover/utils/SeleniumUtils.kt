package com.unrec.lastfm.duplicates.remover.utils

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

fun WebElement.artist() = findXpath(ARTIST_XPATH)
fun WebElement.track() = findXpath(TRACK_XPATH)
fun WebElement.timestamp() = findXpath(TIMESTAMP_XPATH).toLong()

private fun WebElement.findXpath(xpath: String): String =
    findElement(By.xpath(xpath)).getAttribute(VALUE)

fun List<WebElement>.onlyDuplicates(): List<WebElement> {
    return this.zipWithNext().filter {
        it.first.artist() == it.second.artist() &&
                it.first.track() == it.second.track() &&
                it.first.timestamp() - it.second.timestamp() < 10
    }.map { it.second }
}

private const val ARTIST_XPATH = ".//form/input[@name='artist_name']"
private const val TRACK_XPATH = ".//form/input[@name='track_name']"
private const val TIMESTAMP_XPATH = ".//form/input[@name='timestamp']"

private const val VALUE = "value"