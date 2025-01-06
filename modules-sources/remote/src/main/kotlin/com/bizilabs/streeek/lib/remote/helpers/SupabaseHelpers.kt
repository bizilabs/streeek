package com.bizilabs.streeek.lib.remote.helpers

import com.bizilabs.streeek.lib.remote.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import timber.log.Timber

object Supabase {
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

suspend fun <T> safeSupabaseCall(block: suspend () -> T): NetworkResult<T> {
    return try {
        val data = block()
        NetworkResult.Success(data)
    } catch (e: Exception) {
        Timber.e(e, "Supabase Call Failed")
        NetworkResult.Failure(e)
    }
}
