package com.unrec.lastfm.duplicates.remover.utils

import kotlin.system.exitProcess

const val usernameKey = "--username"
const val passwordKey = "--password"
const val startPageKey = "--start-page"

fun Array<String>.asConfig(): Map<String, String> {

    if (this.size % 2 != 0) {
        println("Incorrect parameters are provided")
        exitProcess(1)
    }

    val map = this.toList().chunked(2).associate { it[0] to it[1] }

    if (!map.keys.contains(usernameKey)) {
        println("Username is not specified.")
        exitProcess(1)
    }

    if (!map.keys.contains(passwordKey)) {
        println("Last.fm password is not provided.")
        exitProcess(1)
    }

    if (map[startPageKey] == null) {
        println("Start page is not specified, will start from page 1.")
    }

    return map
}