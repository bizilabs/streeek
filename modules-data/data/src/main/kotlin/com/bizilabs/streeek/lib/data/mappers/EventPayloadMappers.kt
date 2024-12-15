package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.CommitCommentEventDomain
import com.bizilabs.streeek.lib.domain.models.CreateEventDomain
import com.bizilabs.streeek.lib.domain.models.DeleteEventDomain
import com.bizilabs.streeek.lib.domain.models.IssuesEventDomain
import com.bizilabs.streeek.lib.domain.models.PullRequestEventDomain
import com.bizilabs.streeek.lib.domain.models.PushEventDomain
import com.bizilabs.streeek.lib.domain.models.WatchEventDomain
import com.bizilabs.streeek.lib.remote.models.CommitCommentEventDTO
import com.bizilabs.streeek.lib.remote.models.CreateEventDTO
import com.bizilabs.streeek.lib.remote.models.DeleteEventDTO
import com.bizilabs.streeek.lib.remote.models.EventPayloadDTO
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
}

fun CreateEventDTO.toDomain() = CreateEventDomain(
    ref = ref,
    description = description,
    refType = refType,
    pusherType = pusherType
)

fun DeleteEventDTO.toDomain() = DeleteEventDomain(
    ref = ref,
    refType = refType
)

fun CommitCommentEventDTO.toDomain() = CommitCommentEventDomain(
    action = action,
    comment = comment.toDomain()
)

fun PushEventDTO.toDomain() = PushEventDomain(
    id = id,
    size = size,
    distinctSize = distinctSize,
    ref = ref,
    commits = commits.map { it.toDomain() }
)

fun PullRequestEventDTO.toDomain() = PullRequestEventDomain(
    action = action,
    number = number,
    pullRequest = pullRequest.toDomain()
)

fun IssuesEventDTO.toDomain() = IssuesEventDomain(action = action, issue = issue.toDomain())

fun WatchEventDTO.toDomain() = WatchEventDomain(action = action)
