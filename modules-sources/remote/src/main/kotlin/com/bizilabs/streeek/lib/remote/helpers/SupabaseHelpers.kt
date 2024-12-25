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
        const val Accounts = "accounts"
        const val AccountPoints = "account_points"
        const val Contributions = "contributions"
    }
    object Functions {
        const val GetAccountWithPoints = "get_account_with_points_and_level"
        object Teams {
            const val Create = "create_team"
            const val Get = "get_team_with_members"
            const val GetMembersWithAccount = "get_team_with_members_and_account"
            const val Join = "join_team_request"
            object Requests {
                const val Get = "accept_team_request"
            }
        }
    }
}

private val serializer = KotlinXSerializer(Json {
    prettyPrint = true
    isLenient = true
})

fun createSupabase(): SupabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SupabaseUrl,
    supabaseKey = BuildConfig.SupabaseKey
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
