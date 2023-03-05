package com.unrec.lastfm.duplicates.remover.utils

import com.unrec.lastfm.duplicates.remover.model.toScrobbledTrack
import mu.KotlinLogging
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.interactions.Actions
import java.time.Duration

class BrowserFacade {

    private val logger = KotlinLogging.logger {}

    private val driver: ChromeDriver

    init {
        val options = ChromeOptions().apply {
            addArguments("--log-level=3")
        }
        driver = ChromeDriver(options)
        driver.manage().deleteAllCookies()
        driver.manage().window().maximize()
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10))
    }

    fun login(user: String, password: String) {
        driver.get(loginUrl)
        runCatching {
            val cookiePopup = driver.findElement(By.id("onetrust-accept-btn-handler"))
            cookiePopup.click()
        }.onFailure {
            when (it) {
                is NoSuchElementException -> println("No cookie popup")
                else -> throw it
            }
        }

        val userField = driver.findElement(By.id("id_username_or_email"))
        userField.clear()
        userField.sendKeys(user)

        val passwordField = driver.findElement(By.id("id_password"))
        passwordField.clear()
        passwordField.sendKeys(password)
        driver.findElement(By.xpath("//div[@class='form-submit']/button[@class='btn-primary']"))
            .click()
    }

    fun openPage(url: String) {
        driver.get(url)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
    }

    fun getPagesAmount() = driver.findElements(By.className("pagination-page")).last().text.toInt()

    fun getScrobbledTracks(): List<WebElement> =
        driver.findElements(By.xpath("//td[@class='chartlist-more focus-control']"))

    fun deleteScrobble(element: WebElement, page: Int) {
        runCatching {
            val parentElement = element.findElement(By.xpath("./.."))
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20))

            val actions = Actions(driver)
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20))

            actions.moveToElement(parentElement)
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))

            actions.perform()
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))

            element.click()
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))

            val deleteButton = element.findElement(By.className("more-item--delete"))
            deleteButton.click()
        }.onFailure {
            logger.info { "Failed to delete a track: ${element.toScrobbledTrack()}, page: $page" }
        }
    }

    fun close() = driver.close()
}

const val loginUrl = "https://www.last.fm/login"