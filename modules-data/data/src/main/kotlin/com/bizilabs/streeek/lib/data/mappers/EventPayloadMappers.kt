package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.models.CommitCommentEventDomain
import com.bizilabs.streeek.lib.domain.models.CreateEventDomain
import com.bizilabs.streeek.lib.domain.models.DeleteEventDomain
import com.bizilabs.streeek.lib.domain.models.EventPayloadDomain
import com.bizilabs.streeek.lib.domain.models.ForkEventDomain
import com.bizilabs.streeek.lib.domain.models.GollumEventDomain
import com.bizilabs.streeek.lib.domain.models.IssueCommentEventDomain
import com.bizilabs.streeek.lib.domain.models.IssuesEventDomain
import com.bizilabs.streeek.lib.domain.models.MemberEventDomain
import com.bizilabs.streeek.lib.domain.models.PublicEventDomain
import com.bizilabs.streeek.lib.domain.models.PullRequestEventDomain
import com.bizilabs.streeek.lib.domain.models.PullRequestReviewCommentEventDomain
import com.bizilabs.streeek.lib.domain.models.PullRequestReviewEventDomain
import com.bizilabs.streeek.lib.domain.models.PullRequestReviewThreadEventDomain
import com.bizilabs.streeek.lib.domain.models.PushEventDomain
import com.bizilabs.streeek.lib.domain.models.ReleaseEventDomain
import com.bizilabs.streeek.lib.domain.models.SponsorshipEventDomain
import com.bizilabs.streeek.lib.domain.models.WatchEventDomain
import com.bizilabs.streeek.lib.remote.models.CommitCommentEventDTO
import com.bizilabs.streeek.lib.remote.models.CreateEventDTO
import com.bizilabs.streeek.lib.remote.models.DeleteEventDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadDTO
import com.bizilabs.streeek.lib.remote.models.ForkEventDTO
import com.bizilabs.streeek.lib.remote.models.GollumEventDTO
import com.bizilabs.streeek.lib.remote.models.IssueCommentEventDTO
import com.bizilabs.streeek.lib.remote.models.IssuesEventDTO
import com.bizilabs.streeek.lib.remote.models.MemberEventDTO
import com.bizilabs.streeek.lib.remote.models.PublicEventDTO
import com.bizilabs.streeek.lib.remote.models.PullRequestEventDTO
import com.bizilabs.streeek.lib.remote.models.PullRequestReviewCommentEventDTO
import com.bizilabs.streeek.lib.remote.models.PullRequestReviewEventDTO
import com.bizilabs.streeek.lib.remote.models.PullRequestReviewThreadEventDTO
import com.bizilabs.streeek.lib.remote.models.PushEventDTO
import com.bizilabs.streeek.lib.remote.models.ReleaseEventDTO
import com.bizilabs.streeek.lib.remote.models.SponsorshipEventDTO
import com.bizilabs.streeek.lib.remote.models.WatchEventDTO

fun EventPayloadDTO.toDomain() =
    when (this) {
        is CommitCommentEventDTO -> toDomain()
        is CreateEventDTO -> toDomain()
        is DeleteEventDTO -> toDomain()
        is ForkEventDTO -> toDomain()
        is GollumEventDTO -> toDomain()
        is PushEventDTO -> toDomain()
        is PullRequestEventDTO -> toDomain()
        is IssuesEventDTO -> toDomain()
        is WatchEventDTO -> toDomain()
        is PullRequestReviewEventDTO -> toDomain()
        is IssueCommentEventDTO -> toDomain()
        is MemberEventDTO -> toDomain()
        is PublicEventDTO -> toDomain()
        is PullRequestReviewCommentEventDTO -> toDomain()
        is PullRequestReviewThreadEventDTO -> toDomain()
        is ReleaseEventDTO -> toDomain()
        is SponsorshipEventDTO -> toDomain()
    }

fun EventPayloadDomain.toDTO() =
    when (this) {
        is CommitCommentEventDomain -> toDTO()
        is CreateEventDomain -> toDTO()
        is DeleteEventDomain -> toDTO()
        is ForkEventDomain -> toDTO()
        is GollumEventDomain -> toDTO()
        is PushEventDomain -> toDTO()
        is PullRequestEventDomain -> toDTO()
        is IssuesEventDomain -> toDTO()
        is WatchEventDomain -> toDTO()
        is PullRequestReviewEventDomain -> toDTO()
        is IssueCommentEventDomain -> toDTO()
        is MemberEventDomain -> toDTO()
        is PublicEventDomain -> toDTO()
        is PullRequestReviewCommentEventDomain -> toDTO()
        is PullRequestReviewThreadEventDomain -> toDTO()
        is ReleaseEventDomain -> toDTO()
        is SponsorshipEventDomain -> toDTO()
    }

// <editor-fold desc="CommitCommentEvent">
fun CommitCommentEventDTO.toDomain() =
    CommitCommentEventDomain(
        action = action,
        comment = comment.toDomain(),
    )

fun CommitCommentEventDomain.toDTO() =
    CommitCommentEventDTO(
        action = action,
        comment = comment.toDTO(),
    )
// </editor-fold>

// <editor-fold desc="CreateEvent">
fun CreateEventDTO.toDomain() =
    CreateEventDomain(
        ref = ref,
        description = description ?: "",
        refType = refType,
        pusherType = pusherType,
    )

fun CreateEventDomain.toDTO() =
    CreateEventDTO(
        ref = ref,
        description = description,
        refType = refType,
        pusherType = pusherType,
    )
// </editor-fold>

// <editor-fold desc="DeleteEvent">
fun DeleteEventDTO.toDomain() =
    DeleteEventDomain(
        ref = ref,
        refType = refType,
    )

fun DeleteEventDomain.toDTO() =
    DeleteEventDTO(
        ref = ref,
        refType = refType,
    )
// </editor-fold>

// <editor-fold desc="ForkEvent">
fun ForkEventDTO.toDomain() = ForkEventDomain(forkee = forkee.toDomain())

fun ForkEventDomain.toDTO() = ForkEventDTO(forkee = forkee.toDTO())
// </editor-fold>

// <editor-fold desc="GollumEvent">
fun GollumEventDomain.toDTO() = GollumEventDTO(pages = pages.map { it.toDTO() })

fun GollumEventDTO.toDomain() = GollumEventDomain(pages = pages.map { it.toDomain() })
// </editor-fold>

// <editor-fold desc="IssueCommentEvent">
fun IssueCommentEventDTO.toDomain() =
    IssueCommentEventDomain(
        action = action,
        issue = issue.toDomain(),
        comment = comment.toDomain(),
    )

fun IssueCommentEventDomain.toDTO() =
    IssueCommentEventDTO(
        action = action,
        issue = issue.toDTO(),
        comment = comment.toDTO(),
    )
// </editor-fold>

// <editor-fold desc="IssueEvent">
fun IssuesEventDTO.toDomain() = IssuesEventDomain(action = action, issue = issue.toDomain())

fun IssuesEventDomain.toDTO() = IssuesEventDTO(action = action, issue = issue.toDTO())
// </editor-fold>

// <editor-fold desc="MemberEvent">
fun MemberEventDTO.toDomain() = MemberEventDomain(action = action, member = member.toDomain())

fun MemberEventDomain.toDTO() = MemberEventDTO(action = action, member = member.toDTO())
// </editor-fold>

// <editor-fold desc="PublicEvent">
fun PublicEventDTO.toDomain() = PublicEventDomain()

fun PublicEventDomain.toDTO() = PublicEventDTO()
// </editor-fold>

// <editor-fold desc="PullRequestEvent">
fun PullRequestEventDTO.toDomain() =
    PullRequestEventDomain(
        action = action,
        pullRequest = pullRequest.toDomain(),
        reason = reason,
    )

fun PullRequestEventDomain.toDTO() =
    PullRequestEventDTO(
        action = action,
        pullRequest = pullRequest.toDTO(),
        reason = reason,
    )
// </editor-fold>

// <editor-fold desc="PullRequestReviewEvent">
fun PullRequestReviewEventDTO.toDomain() =
    PullRequestReviewEventDomain(
        action = action,
        review = review.toDomain(),
        pullRequest = pullRequest.toDomain(),
    )

fun PullRequestReviewEventDomain.toDTO() =
    PullRequestReviewEventDTO(
        action = action,
        review = review.toDTO(),
        pullRequest = pullRequest.toDTO(),
    )
// </editor-fold>

// <editor-fold desc="PullRequestReviewCommentEvent">
fun PullRequestReviewCommentEventDTO.toDomain() =
    PullRequestReviewCommentEventDomain(
        action = action,
        comment = comment.toDomain(),
        pullRequest = pullRequest.toDomain(),
    )

fun PullRequestReviewCommentEventDomain.toDTO() =
    PullRequestReviewCommentEventDTO(
        action = action,
        comment = comment.toDTO(),
        pullRequest = pullRequest.toDTO(),
    )
// </editor-fold>

// <editor-fold desc="PullRequestReviewThreadEvent">
fun PullRequestReviewThreadEventDTO.toDomain() = PullRequestReviewThreadEventDomain(action = action, pullRequest = pullRequest.toDomain())

fun PullRequestReviewThreadEventDomain.toDTO() = PullRequestReviewThreadEventDTO(action = action, pullRequest = pullRequest.toDTO())
// </editor-fold>

// <editor-fold desc="PushEvent">
fun PushEventDTO.toDomain() =
    PushEventDomain(
        id = id,
        size = size,
        distinctSize = distinctSize,
        ref = ref,
        commits = commits.map { it.toDomain() },
    )

fun PushEventDomain.toDTO() =
    PushEventDTO(
        id = id,
        size = size,
        distinctSize = distinctSize,
        ref = ref,
        commits = commits.map { it.toDTO() },
    )
// </editor-fold>

// <editor-fold desc="ReleaseEvent">
fun ReleaseEventDTO.toDomain() =
    ReleaseEventDomain(
        action = action,
        release = release.toDomain(),
    )

fun ReleaseEventDomain.toDTO() =
    ReleaseEventDTO(
        action = action,
        release = release.toDTO(),
    )
// </editor-fold>

// <editor-fold desc="SponsorshipEvent">
fun SponsorshipEventDTO.toDomain() =
    SponsorshipEventDomain(
        action = action,
        effectiveDate = effectiveDate.asDate()?.datetimeSystem ?: SystemLocalDateTime,
    )

fun SponsorshipEventDomain.toDTO() =
    SponsorshipEventDTO(
        action = action,
        effectiveDate = effectiveDate.asString() ?: "",
    )
// </editor-fold>

// <editor-fold desc="WatchEvent">
fun WatchEventDTO.toDomain() = WatchEventDomain(action = action)

fun WatchEventDomain.toDTO() = WatchEventDTO(action = action)
// </editor-fold>
