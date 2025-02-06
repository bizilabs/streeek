package com.bizilabs.streeek.lib.domain.helpers

fun buildUri(vararg paths: Pair<String, Any>): String =
    buildString {
        append("https://app.streeek.com/v1")
        if (paths.isNotEmpty()) append("?")
        paths.forEachIndexed { index, (key, value) ->
            append("$key=$value")
            if (index != paths.lastIndex) append("&")
        }
    }
