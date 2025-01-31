package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.UnknownHostException

internal object Supabase {
    object Tables {
        const val ACCOUNTS = "accounts"
        const val CONTRIBUTIONS = "contributions"
        const val LEVELS = "levels"
        const val NOTIFICATIONS = "notifications"
        const val TEAM_REQUESTS = "team_member_requests"
    }

    object Functions {
        const val GETACCOUNTWITHPOINTS = "get_account_with_points_and_level"

        object Notifications {
            const val CREATE = "insert_notification"
        }

        object Teams {
            const val CREATE = "create_team"
            const val UPDATE = "update_team"
            const val GETMEMBERSWITHACCOUNT = "get_team_with_members_and_account"
            const val GETACCOUNTTEAMS = "get_teams_for_account"
            const val JOIN = "join_team_with_invite_code"
            const val LEAVE = "leave_team"
            const val DELETE = "archive_team"
            const val GETTEAMANDMEMBERS = "get_teams_with_member_count_and_members"

            object MemberRequests {
                const val GETTEAMREQUESTS = "get_team_join_requests"
                const val GETMYREQUESTS = "get_my_member_requests"
                const val PROCESSMYREQUEST = "process_single_member_request"
                const val PROCESSSINGLEREQUEST = "process_member_request"
                const val PROCESSMULTIPLTREQUEST = "process_member_requests"
                const val REQUESTTOJOINTEAM = "request_to_join_team"
            }

            object CodeInvitations {
                const val GET = "get_team_invitations"
                const val DELETE = "delete_team_invitation"
                const val CREATE = "create_team_invite_code"
            }

            object MemberInvitations {
                const val GETACCOUNTSNOTINTEAM = "get_accounts_not_in_team"
                const val SEARCHACCOUNTSNOTINTEAM = "search_accounts_not_in_team"
                const val SENDMEMBERINVITE = "send_member_team_invite"
                const val SENDMULTIPLEMEMBERINVITE = "send_multiple_member_team_invite"
                const val DELETEINVITE = "delete_member_team_invite"
                const val UPDATEINVITESTATUS = "update_invite_status"
                const val UPDATEMULTIPLEINVITESTATUS = "update_multiple_invite_status"
                const val GETACCOUNTINVITES = "get_all_invites_for_account"
                const val GETTEAMACCOUNTINVITES = "get_team_account_invites"
            }
        }

        object Leaderboard {
            const val DAILY = "get_daily_leaderboard_with_account"
            const val WEEKLY = "get_weekly_leaderboard_with_account"
            const val MONTHLY = "get_monthly_leaderboard_with_account"
            const val ULTIMATE = "get_all_time_leaderboard_with_account"
        }
    }

    object ErrorMessages {
        const val BAD_REQUEST_EXCEPTION =
            "You have entered an invalid code, please double check or " +
                "ask the admin to share the correct code."

        const val UNKNOWN_HOST_EXCEPTION =
            "You seem to have no network connection. \nPlease connect to a network to continue."
    }
}

private val serializer =
    KotlinXSerializer(
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        },
    )

fun createSupabase(): SupabaseClient =
    createSupabaseClient(
        supabaseUrl = BuildConfig.SupabaseUrl,
        supabaseKey = BuildConfig.SupabaseKey,
    ) {
        install(Auth)
        install(Storage)
        install(Realtime)
        install(Postgrest)
        install(Functions)
        defaultSerializer = serializer
    }

private fun String?.toValidErrorMessage(default: String): String {
    return when {
        this.isNullOrBlank() -> default
        this.contains(
            "unable to resolve host",
            true,
        ) -> Supabase.ErrorMessages.UNKNOWN_HOST_EXCEPTION

        this.contains(BuildConfig.SupabaseUrl) -> this.replace(BuildConfig.SupabaseUrl, "********")
        else -> this
    }
}

suspend fun <T> safeSupabaseCall(
    defaultErrorMessage: String = "supabase failed",
    block: suspend () -> T,
): NetworkResult<T> {
    return try {
        val data = block()
        NetworkResult.Success(data)
    } catch (e: NotFoundRestException) {
        Timber.e(e, "Resource not found.")
        NetworkResult.Failure(Exception("Resource Not Found!"))
    } catch (e: BadRequestRestException) {
        Timber.e(e, "Bad Request.")
        NetworkResult.Failure(Exception(Supabase.ErrorMessages.BAD_REQUEST_EXCEPTION))
    } catch (e: UnknownHostException) {
        Timber.e(e, "Unknown Host.")
        NetworkResult.Failure(Exception(Supabase.ErrorMessages.UNKNOWN_HOST_EXCEPTION))
    } catch (e: Exception) {
        Timber.e(e, "Supabase Call Failed")
        val message = e.message.toValidErrorMessage(default = defaultErrorMessage)
        NetworkResult.Failure(Exception(message))
    }
}
