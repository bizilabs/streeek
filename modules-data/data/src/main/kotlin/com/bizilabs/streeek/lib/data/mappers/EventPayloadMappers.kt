package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.CommitCommentEventDomain
import com.bizilabs.streeek.lib.domain.models.CreateEventDomain
import com.bizilabs.streeek.lib.domain.models.DeleteEventDomain
import com.bizilabs.streeek.lib.domain.models.EventPayloadDomain
import com.bizilabs.streeek.lib.domain.models.ForkEventDomain
import com.bizilabs.streeek.lib.domain.models.IssuesEventDomain
import com.bizilabs.streeek.lib.domain.models.PullRequestEventDomain
import com.bizilabs.streeek.lib.domain.models.PushEventDomain
import com.bizilabs.streeek.lib.domain.models.WatchEventDomain
import com.bizilabs.streeek.lib.remote.models.CommitCommentEventDTO
import com.bizilabs.streeek.lib.remote.models.CreateEventDTO
import com.bizilabs.streeek.lib.remote.models.DeleteEventDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadDTO
import com.bizilabs.streeek.lib.remote.models.ForkEventDTO
import com.bizilabs.streeek.lib.remote.models.IssuesEventDTO
import com.bizilabs.streeek.lib.remote.models.PullRequestEventDTO
import com.bizilabs.streeek.lib.remote.models.PushEventDTO
import com.bizilabs.streeek.lib.remote.models.WatchEventDTO

fun EventPayloadDTO.toDomain() = when (this) {
    is CreateEventDTO -> toDomain()
    is DeleteEventDTO -> toDomain()
    is CommitCommentEventDTO -> toDomain()
    is PushEventDTO -> toDomain()
    is PullRequestEventDTO -> toDomain()
    is IssuesEventDTO -> toDomain()
    is WatchEventDTO -> toDomain()
    is ForkEventDTO -> toDomain()
}

fun EventPayloadDomain.toDTO() = when (this) {
    is CreateEventDomain -> toDTO()
    is DeleteEventDomain -> toDTO()
    is CommitCommentEventDomain -> toDTO()
    is PushEventDomain -> toDTO()
    is PullRequestEventDomain -> toDTO()
    is IssuesEventDomain -> toDTO()
    is WatchEventDomain -> toDTO()
    is ForkEventDomain -> toDTO()
}

fun CreateEventDTO.toDomain() = CreateEventDomain(
    ref = ref,
    description = description ?: "",
    refType = refType,
    pusherType = pusherType
)

fun CreateEventDomain.toDTO() = CreateEventDTO(
    ref = ref,
    description = description,
    refType = refType,
    pusherType = pusherType
)

fun DeleteEventDTO.toDomain() = DeleteEventDomain(
    ref = ref,
    refType = refType
)

fun DeleteEventDomain.toDTO() = DeleteEventDTO(
    ref = ref,
    refType = refType
)

fun CommitCommentEventDTO.toDomain() = CommitCommentEventDomain(
    action = action,
    comment = comment.toDomain()
)

fun CommitCommentEventDomain.toDTO() = CommitCommentEventDTO(
    action = action,
    comment = comment.toDTO()
)

fun PushEventDTO.toDomain() = PushEventDomain(
    id = id,
    size = size,
    distinctSize = distinctSize,
    ref = ref,
    commits = commits.map { it.toDomain() }
)

fun PushEventDomain.toDTO() = PushEventDTO(
    id = id,
    size = size,
    distinctSize = distinctSize,
    ref = ref,
    commits = commits.map { it.toDTO() }
)

fun PullRequestEventDTO.toDomain() = PullRequestEventDomain(
    action = action,
    number = number,
    pullRequest = pullRequest.toDomain()
)

fun PullRequestEventDomain.toDTO() = PullRequestEventDTO(
    action = action,
    number = number,
    pullRequest = pullRequest.toDTO()
)

fun IssuesEventDTO.toDomain() = IssuesEventDomain(action = action, issue = issue.toDomain())
fun IssuesEventDomain.toDTO() = IssuesEventDTO(action = action, issue = issue.toDTO())

fun WatchEventDTO.toDomain() = WatchEventDomain(action = action)
fun WatchEventDomain.toDTO() = WatchEventDTO(action = action)

fun ForkEventDTO.toDomain() = ForkEventDomain(forkee = forkee.toDomain())
fun ForkEventDomain.toDTO() = ForkEventDTO(forkee = forkee.toDTO())
