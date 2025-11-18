package com.webmd.newsfeed.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun String.formatDate(pattern: String = "MMM dd, yyyy • hh:mm a"): String {
    return try {
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = isoFormat.parse(this)

        val outputFormat = SimpleDateFormat(pattern, Locale.getDefault())
        outputFormat.timeZone = TimeZone.getDefault()

        outputFormat.format(date!!)
    } catch (_: Exception) {
        this
    }
}

