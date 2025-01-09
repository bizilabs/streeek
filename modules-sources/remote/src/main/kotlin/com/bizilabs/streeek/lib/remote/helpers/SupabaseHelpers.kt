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

internal object Supabase {
    object Tables {
        const val ACCOUNTS = "accounts"
        const val ACCOUNTPOINTS = "account_points"
        const val CONTRIBUTIONS = "contributions"
        const val LEVELS = "levels"
        const val NOTIFICATIONS = "notifications"
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

            object Invitations {
                const val GET = "get_team_invitations"
                const val DELETE = "delete_team_invitation"
                const val CREATE = "create_team_invite_code"
            }
        }

        object Leaderboard {
            const val DAILY = "get_daily_leaderboard_with_account"
            const val WEEKLY = "get_weekly_leaderboard_with_account"
            const val MONTHLY = "get_monthly_leaderboard_with_account"
        }
    }

    object ErrorMessages {
        const val BAD_REQUEST_EXCEPTION =
            "You have entered an invalid code, please double check or " +
                "ask the admin to share the correct code."
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
    } catch (e: Exception) {
        Timber.e(e, "Supabase Call Failed")
        val message = e.message.toValidErrorMessage(default = defaultErrorMessage)
        NetworkResult.Failure(Exception(message))
    }
}
