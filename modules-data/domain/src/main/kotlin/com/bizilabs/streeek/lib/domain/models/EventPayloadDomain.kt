package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

sealed interface EventPayloadDomain {
    fun getPoints(account: AccountDomain): Long
}

data class CommitCommentEventDomain(
    val action: String,
    val comment: CommitCommentDomain
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long =
        if (action.equals("created", true)) 10 else 0
}

data class CreateEventDomain(
    val ref: String?,
    val description: String,
    val refType: String,
    val pusherType: String
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long =
        if (pusherType.equals("user", true)) 20 else 0
}

data class DeleteEventDomain(
    val ref: String,
    val refType: String
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = -10
}

data class ForkEventDomain(val forkee: EventRepositoryDomain) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 10
}

data class GollumEventDomain(
    val pages: List<GollumPageDomain>
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 10
}

data class IssueCommentEventDomain(
    val action: String,
    val issue: IssueDomain,
    val comment: CommentDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 10
}

data class IssuesEventDomain(
    val action: String,
    val issue: IssueDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("opened", true) -> 50
            action.equals("edited", true) -> 20
            else -> 0
        }
    }
}

data class MemberEventDomain(
    val action: String,
    val member: ActorDomain
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = when (member.id) {
        account.githubId -> 10
        else -> 0
    }
}

class PublicEventDomain() : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 20
}

data class PullRequestEventDomain(
    val action: String,
    val pullRequest: MinPullRequestDomain,
    val reason: String?
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("opened", true) -> 50
            action.equals("edited", true) -> 20
            else -> 0
        }
    }
}

data class PullRequestReviewEventDomain(
    val action: String,
    val review: ReviewDomain,
    val pullRequest: MinPullRequestDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return 10
    }
}

data class PullRequestReviewCommentEventDomain(
    val action: String,
    val comment: CommentDomain,
    val pullRequest: MinPullRequestDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("created", true) -> 20
            action.equals("edited", true) -> 10
            else -> 0
        }
    }
}

data class PullRequestReviewThreadEventDomain(
    val action: String,
    val pullRequest: MinPullRequestDomain
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("resolved", true) -> 20
            action.equals("unresolved", true) -> 10
            else -> 0
        }
    }
}

data class PushEventDomain(
    val id: Long,
    val size: Long,
    val distinctSize: Long,
    val ref: String,
    val commits: List<CommitDomain>
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return commits.map {
            if (it.author.name.equals(account.username, true))
                10L
            else
                0L
        }.sum()
    }
}

data class ReleaseEventDomain(
    val action: String,
    val release: ReleaseDomain
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("published", true) -> 50
            else -> 0
        }
    }
}

data class SponsorshipEventDomain(
    val action: String,
    val effectiveDate: LocalDateTime
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("created", true) -> 100
            else -> 0
        }
    }
}

data class WatchEventDomain(val action: String) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("started", true) -> 10
            else -> 0
        }
    }
}
