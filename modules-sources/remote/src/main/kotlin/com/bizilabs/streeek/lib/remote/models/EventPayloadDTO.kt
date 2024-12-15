package com.bizilabs.streeek.lib.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = EventPayloadSerializer::class)
sealed interface EventPayloadDTO

@Serializable
data class CreateEventDTO(
    val ref: String? = "repository",
    val description: String,
    @SerialName("ref_type") val refType: String,
    @SerialName("pusher_type") val pusherType: String
) : EventPayloadDTO

@Serializable
data class DeleteEventDTO(
    val ref: String,
    @SerialName("ref_type")
    val refType: String
) : EventPayloadDTO

@Serializable
data class CommitCommentEventDTO(
    val action: String,
    val comment: CommitCommentDTO
) : EventPayloadDTO

@Serializable
data class PushEventDTO(
    @SerialName("push_id")
    val id: Long,
    val size: Long,
    @SerialName("distinct_size")
    val distinctSize: Long,
    val ref: String,
    val commits: List<CommitDTO>
) : EventPayloadDTO

@Serializable
data class PullRequestEventDTO(
    val action: String,
    val number: Long,
    @SerialName("pull_request")
    val pullRequest: PullRequestDTO
) : EventPayloadDTO

@Serializable
data class IssuesEventDTO(
    val action: String,
    val issue: GithubIssueDTO,
) : EventPayloadDTO

@Serializable
data class WatchEventDTO(val action: String) : EventPayloadDTO

object EventPayloadSerializer : JsonContentPolymorphicSerializer<EventPayloadDTO>(EventPayloadDTO::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "comment" in element.jsonObject.keys -> CommitCommentEventDTO.serializer()
        "commits" in element.jsonObject.keys -> PushEventDTO.serializer()
        "pull_request" in element.jsonObject.keys -> PullRequestEventDTO.serializer()
        "issue" in element.jsonObject.keys -> IssuesEventDTO.serializer()
        "pusher_type" in element.jsonObject.keys && "master_branch" in element.jsonObject.keys -> CreateEventDTO.serializer()
        "ref_type" in element.jsonObject.keys -> DeleteEventDTO.serializer()
        else -> WatchEventDTO.serializer()
    }
}
