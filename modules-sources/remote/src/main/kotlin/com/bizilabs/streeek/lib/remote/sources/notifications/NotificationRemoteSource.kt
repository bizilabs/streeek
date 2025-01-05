package com.bizilabs.streeek.lib.remote.sources.notifications

import com.bizilabs.streeek.lib.remote.helpers.NetworkResult
import com.bizilabs.streeek.lib.remote.helpers.Supabase
import com.bizilabs.streeek.lib.remote.helpers.range
import com.bizilabs.streeek.lib.remote.helpers.safeSupabaseCall
import com.bizilabs.streeek.lib.remote.models.supabase.NotificationDTO
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

interface NotificationRemoteSource {
    suspend fun fetchNotifications(accountId: Long, page: Int): NetworkResult<List<NotificationDTO>>
}

internal class NotificationRemoteSourceImpl(
    private val supabase: SupabaseClient
) : NotificationRemoteSource {
    override suspend fun fetchNotifications(
        accountId: Long,
        page: Int
    ): NetworkResult<List<NotificationDTO>> =
        safeSupabaseCall {
            supabase
                .from(Supabase.Tables.Notifications)
                .select {
                    filter { NotificationDTO::accountId eq accountId }
                    order(column = NotificationDTO.Columns.CreatedAt, order = Order.DESCENDING)
                    range(page = page)
                }
                .decodeList()
        }
}
