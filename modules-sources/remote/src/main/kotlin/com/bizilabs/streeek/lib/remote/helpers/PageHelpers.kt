package com.bizilabs.streeek.lib.remote.helpers

const val PAGE_SIZE: Int = 20

data class PageRange(
    val page: Int,
) {
    val offset: Long
        get() = (page.toLong() - 1) * PAGE_SIZE

    val from: Long
        get() = offset

    val to: Long
        get() = offset + PAGE_SIZE - 1
}

fun getRange(page: Int): PageRange {
    return PageRange(page)
}
