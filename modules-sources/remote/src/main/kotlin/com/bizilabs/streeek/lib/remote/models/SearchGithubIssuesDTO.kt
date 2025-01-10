package com.bizilabs.streeek.lib.remote.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchGithubIssuesDTO(
    @SerialName("incomplete_results")
    val incompleteResults: Boolean?, // false
    @SerialName("items")
    val items: List<GithubIssueDTO>,
    @SerialName("total_count")
    val totalCount: Int?
)