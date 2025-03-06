package com.bizilabs.streeek.lib.domain.extensions

fun Long.asRank() =
    when {
        this < 10 -> "0$this"
        else -> this.toString()
    }

fun Long.asMinimumTwoValues() =
    when {
        this < 10 -> "0$this"
        else -> this.toString()
    }
