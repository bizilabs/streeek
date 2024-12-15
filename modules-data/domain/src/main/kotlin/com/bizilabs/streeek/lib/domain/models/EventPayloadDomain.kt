package com.bizilabs.streeek.lib.domain.models

sealed interface EventPayloadDomain

data class CreateEventDomain(
    val ref: String?,
    val description: String,
    val refType: String,
    val pusherType: String
) : EventPayloadDomain

data class DeleteEventDomain(
    val ref: String,
    val refType: String
) : EventPayloadDomain

data class CommitCommentEventDomain(
    val action: String,
    val comment: CommitCommentDomain
) : EventPayloadDomain

data class PushEventDomain(
    val id: Long,
    val size: Long,
    val distinctSize: Long,
    val ref: String,
    val commits: List<CommitDomain>
) : EventPayloadDomain

data class PullRequestEventDomain(
    val action: String,
    val number: Long,
    val pullRequest: PullRequestDomain
) : EventPayloadDomain

data class IssuesEventDomain(
    val action: String,
    val issue: IssueDomain,
) : EventPayloadDomain

data class WatchEventDomain(val action: String) : EventPayloadDomain
