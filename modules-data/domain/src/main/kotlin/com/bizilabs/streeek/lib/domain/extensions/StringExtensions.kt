package com.bizilabs.streeek.lib.domain.extensions

fun String.asCount(count: Int) = this + if (count > 1) "s" else ""

fun String.asCount(count: Long) = this + if (count > 1) "s" else ""
