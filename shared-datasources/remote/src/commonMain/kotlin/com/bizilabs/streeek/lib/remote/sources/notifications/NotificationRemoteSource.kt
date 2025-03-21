package com.bizilabs.streeek.lib.remote.sources.notifications

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.asJsonObject
import com.bizilabs.streeek.lib.remote.helpers.range
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.NotificationCreateDTO
import com.bizilabs.streeek.lib.remote.models.supabase.NotificationDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

interface NotificationRemoteSource {
    suspend fun fetchNotifications(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<NotificationDTO>>

    suspend fun create(request: NotificationCreateDTO): NetworkResult<NotificationDTO>

    suspend fun update(notification: NotificationDTO): NetworkResult<NotificationDTO>

    suspend fun delete(notification: NotificationDTO): NetworkResult<Boolean>
}

internal class NotificationRemoteSourceImpl(
    private val supabase: SupabaseClient,
) : NotificationRemoteSource {
    override suspend fun fetchNotifications(
        accountId: Long,
        page: Int,
    ): NetworkResult<List<NotificationDTO>> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.NOTIFICATIONS)
                .select {
                    filter { NotificationDTO::accountId eq accountId }
                    order(column = NotificationDTO.Columns.CreatedAt, order = Order.DESCENDING)
                    range(page = page)
                }
                .decodeList()
        }

    override suspend fun create(request: NotificationCreateDTO): NetworkResult<NotificationDTO> =
        safeSupabaseCall {
            supabase.postgrest
                .rpc(
                    function = Supabase.Functions.Notifications.CREATE,
                    parameters = request.asJsonObject(),
                )
                .decodeAs()
        }

    override suspend fun update(notification: NotificationDTO): NetworkResult<NotificationDTO> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.NOTIFICATIONS)
                .update(
                    {
                        NotificationDTO::title setTo notification.title
                        NotificationDTO::message setTo notification.message
                        NotificationDTO::payload setTo notification.payload
                        NotificationDTO::readAt setTo notification.readAt
                    },
                ) {
                    select()
                    filter {
                        NotificationDTO::id eq notification.id
                    }
                }.decodeSingle<NotificationDTO>()
        }

    override suspend fun delete(notification: NotificationDTO): NetworkResult<Boolean> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.NOTIFICATIONS)
                .delete {
                    filter {
                        NotificationDTO::id eq notification.id
                    }
                }
            true
        }
}
