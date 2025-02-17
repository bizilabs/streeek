package com.bizilabs.streeek.lib.domain.models

import kotlinx.datetime.LocalDateTime

sealed interface EventPayloadDomain {
    fun getPoints(account: AccountDomain): Long
}

data class CommitCommentEventDomain(
    val action: String,
    val comment: CommitCommentDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = if (action.equals("created", true)) 5 else 0
}

data class CreateEventDomain(
    val ref: String?,
    val description: String,
    val refType: String,
    val pusherType: String,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long =
        when {
            pusherType.equals("user", true) -> {
                when {
                    refType.equals("repository", true) -> 15
                    refType.equals("tag", true) -> 20
                    refType.equals("branch", true) -> 10
                    else -> 5
                }
            }

            else -> 0
        }
}

data class DeleteEventDomain(
    val ref: String,
    val refType: String,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long =
        when {
            refType.equals("tag", true) -> -10
            refType.equals("branch", true) -> 5
            else -> 0
        }
}

data class ForkEventDomain(val forkee: EventRepositoryDomain) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 15
}

data class GollumEventDomain(
    val pages: List<GollumPageDomain>,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 20
}

data class IssueCommentEventDomain(
    val action: String,
    val issue: IssueDomain,
    val comment: CommentDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 5
}

data class IssuesEventDomain(
    val action: String,
    val issue: IssueDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("opened", true) -> 30
            action.equals("edited", true) -> 10
            action.equals("closed", true) -> 30
            else -> 0
        }
    }
}

data class MemberEventDomain(
    val action: String,
    val member: ActorDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long =
        when {
            action.equals("added", true) -> 20
            else -> 0
        }
}

class PublicEventDomain() : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long = 15
}

data class PullRequestEventDomain(
    val action: String,
    val pullRequest: MinPullRequestDomain,
    val reason: String?,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("closed", true) -> 50
            action.equals("opened", true) -> 40
            action.equals("reopened", true) -> 30
            action.equals("edited", true) -> 10
            action.equals("labeled", true) -> 5
            action.equals("unlabeled", true) -> -5
            else -> 5
        }
    }
}

data class PullRequestReviewEventDomain(
    val action: String,
    val review: ReviewDomain,
    val pullRequest: MinPullRequestDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long =
        when {
            action.equals("created", true) -> 20
            else -> 5
        }
}

data class PullRequestReviewCommentEventDomain(
    val action: String,
    val comment: CommentDomain,
    val pullRequest: MinPullRequestDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("created", true) -> 15
            action.equals("edited", true) -> 5
            else -> 0
        }
    }
}

data class PullRequestReviewThreadEventDomain(
    val action: String,
    val pullRequest: MinPullRequestDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("resolved", true) -> 25
            action.equals("unresolved", true) -> -30
            else -> 0
        }
    }
}

data class PushEventDomain(
    val id: Long,
    val size: Long,
    val distinctSize: Long,
    val ref: String,
    val commits: List<CommitDomain>,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return commits.filter { it.distinct == true }.map { 5L }.sum()
    }
}

data class ReleaseEventDomain(
    val action: String,
    val release: ReleaseDomain,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("published", true) -> 100
            else -> 0
        }
    }
}

data class SponsorshipEventDomain(
    val action: String,
    val effectiveDate: LocalDateTime,
) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("created", true) -> 150
            else -> 0
        }
    }
}

data class WatchEventDomain(val action: String) : EventPayloadDomain {
    override fun getPoints(account: AccountDomain): Long {
        return when {
            action.equals("started", true) -> 5
            else -> 0
        }
    }
}
