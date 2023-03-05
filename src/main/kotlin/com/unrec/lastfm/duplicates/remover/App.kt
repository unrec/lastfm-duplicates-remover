package com.unrec.lastfm.duplicates.remover

import com.unrec.lastfm.duplicates.remover.model.toScrobbledTrack
import com.unrec.lastfm.duplicates.remover.utils.*
import mu.KotlinLogging
import org.openqa.selenium.WebElement

fun main(args: Array<String>) {
    val settings = args.asConfig()
    val username = settings[usernameKey]!!
    val password = settings[passwordKey]!!
    val startPage = settings[startPageKey]?.toInt() ?: 1

    browser.login(username, password)
    deleteDuplicates(username, startPage)
    browser.close()
}

fun deleteDuplicates(username: String, startPage: Int) {

    var currentPage = startPage
    browser.openPage(userPageUrl(username, currentPage))
    var pagesTotal = browser.getPagesAmount()

    while (currentPage <= pagesTotal) {
        logger.info { "Processing page $currentPage" }

        browser.openPage(userPageUrl(username, currentPage))
        val scrobbledElements = browser.getScrobbledTracks()
        scrobbledElements.onlyDuplicates().also {
            if (it.isNotEmpty()) {
                it.forEach { element -> browser.deleteScrobble(element, currentPage) }
                it.map(WebElement::toScrobbledTrack)
                    .forEach { logger.info { "Duplicate removed: $it" } }
            } else {
                logger.info { "Page $currentPage was processed successfully" }
                currentPage++
                pagesTotal = browser.getPagesAmount()
            }
        }
    }
}

private val browser = BrowserFacade()
private val logger = KotlinLogging.logger {}

private fun userPageUrl(username: String, page: Int) =
    "https://www.last.fm/user/$username/library?page=$page"