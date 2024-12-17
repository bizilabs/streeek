package com.bizilabs.streeek.lib.domain.models

import kotlinx.serialization.json.JsonObject

sealed interface EventPayloadDomain {
    val points: Long
}

data class CreateEventDomain(
    val ref: String?,
    val description: String,
    val refType: String,
    val pusherType: String
) : EventPayloadDomain {
    override val points: Long
        get() = if (pusherType.equals("user", true)) 20 else 0
}

data class DeleteEventDomain(
    val ref: String,
    val refType: String
) : EventPayloadDomain {
    override val points: Long
        get() = -10
}

data class CommitCommentEventDomain(
    val action: String,
    val comment: CommitCommentDomain
) : EventPayloadDomain {
    override val points: Long
        get() = if (action.equals("created", true)) 10 else 0
}

data class PushEventDomain(
    val id: Long,
    val size: Long,
    val distinctSize: Long,
    val ref: String,
    val commits: List<CommitDomain>
) : EventPayloadDomain {
    override val points: Long
        get() = commits.map { 20 }.sum().toLong()
}

data class PullRequestEventDomain(
    val action: String,
    val number: Long,
    val pullRequest: PullRequestDomain
) : EventPayloadDomain {
    override val points: Long
        get() = when {
            action.equals("opened", true) -> 50
            action.equals("edited", true) -> 20
            else -> 0
        }
}

data class IssuesEventDomain(
    val action: String,
    val issue: IssueDomain,
) : EventPayloadDomain {
    override val points: Long
        get() = when {
            action.equals("opened", true) -> 50
            action.equals("edited", true) -> 20
            else -> 0
        }
}

data class WatchEventDomain(val action: String) : EventPayloadDomain {
    override val points: Long
        get() = when {
            action.equals("started", true) -> 10
            else -> 0
        }
}

data class ForkEventDomain(val forkee: JsonObject) : EventPayloadDomain {
    override val points: Long
        get() = 0
}