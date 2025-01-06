package com.bizilabs.streeek.lib.remote.models

import com.bizilabs.streeek.lib.remote.helpers.asJson
import com.bizilabs.streeek.lib.remote.models.github.GithubReleaseDTO
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import timber.log.Timber
import java.io.Serial

@Serializable(with = EventPayloadSerializer::class)
sealed interface EventPayloadDTO

@Serializable
data class CommitCommentEventDTO(
    val action: String,
    val comment: CommitCommentDTO
) : EventPayloadDTO

@Serializable
data class CreateEventDTO(
    val ref: String? = "repository",
    val description: String? = null,
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
data class ForkEventDTO(
    val forkee: GithubEventRepositoryDTO
) : EventPayloadDTO

@Serializable
data class GollumEventDTO(
    val pages: List<GollumPageDTO>
) : EventPayloadDTO

@Serializable
data class IssueCommentEventDTO(
    val action: String,
    val issue: GithubIssueDTO,
    val comment: CommentDTO,
) : EventPayloadDTO

@Serializable
data class IssuesEventDTO(
    val action: String,
    val issue: GithubIssueDTO,
) : EventPayloadDTO

@Serializable
data class MemberEventDTO(
    val action: String,
    val member: GithubActorDTO,
) : EventPayloadDTO

@Serializable
class PublicEventDTO() : EventPayloadDTO

@Serializable
data class PullRequestEventDTO(
    val action: String,
    @SerialName("pull_request")
    val pullRequest: MinPullRequestDTO,
    val reason: String?
) : EventPayloadDTO

@Serializable
data class PullRequestReviewEventDTO(
    val action: String,
    val review: GithubReviewDTO,
    @SerialName("pull_request")
    val pullRequest: MinPullRequestDTO,
) : EventPayloadDTO

@Serializable
data class PullRequestReviewCommentEventDTO(
    val action: String,
    val comment: CommentDTO,
    @SerialName("pull_request")
    val pullRequest: MinPullRequestDTO,
) : EventPayloadDTO

@Serializable
data class PullRequestReviewThreadEventDTO(
    val action: String,
    @SerialName("pull_request")
    val pullRequest: MinPullRequestDTO
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
data class ReleaseEventDTO(
    val action: String,
    val release: GithubReleaseDTO
) : EventPayloadDTO

@Serializable
data class SponsorshipEventDTO(
    val action: String,
    @SerialName("effective_date")
    val effectiveDate: String
) : EventPayloadDTO

@Serializable
data class WatchEventDTO(val action: String) : EventPayloadDTO

object EventPayloadSerializer :
    JsonContentPolymorphicSerializer<EventPayloadDTO>(EventPayloadDTO::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out EventPayloadDTO> {
        Timber.d("Kachari : Serializing -> $element")
        val keys = element.jsonObject.keys
        return when {
            "forkee" in keys -> ForkEventDTO.serializer()
            "pages" in keys -> GollumEventDTO.serializer()
            "member" in keys -> MemberEventDTO.serializer()
            "release" in keys -> ReleaseEventDTO.serializer()
            "effective_date" in keys -> SponsorshipEventDTO.serializer()
            // commit events
            "comment" in keys && "issue" !in keys -> CommitCommentEventDTO.serializer()
            "commits" in keys -> PushEventDTO.serializer()
            // create & delete events
            "ref_type" in keys && "pusher_type" in keys -> CreateEventDTO.serializer()
            "ref_type" in keys -> DeleteEventDTO.serializer()
            // issue events
            "issue" in keys && "comment" in keys -> IssueCommentEventDTO.serializer()
            "issue" in keys -> IssuesEventDTO.serializer()
            // pull request events
            "pull_request" in keys && ("reason" in keys || "number" in keys) -> PullRequestEventDTO.serializer()
            "pull_request" in keys && "review" in keys -> PullRequestReviewEventDTO.serializer()
            "pull_request" in keys && "comment" in keys -> PullRequestReviewCommentEventDTO.serializer()
            "pull_request" in keys && "thread" in keys-> PullRequestReviewThreadEventDTO.serializer()
            "pull_request" in keys  -> PullRequestEventDTO.serializer()
            // others
            keys.isEmpty() -> PublicEventDTO.serializer()
            else -> WatchEventDTO.serializer()
        }.also {
            Timber.d("Kachari : Serialized to -> $it")
            it
        }
    }
}
