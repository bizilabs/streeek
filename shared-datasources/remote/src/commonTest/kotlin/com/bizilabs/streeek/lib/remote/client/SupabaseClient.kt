package com.bizilabs.streeek.lib.remote.client

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.http.HttpStatusCode

fun fakeSupabaseClient(
    status: HttpStatusCode,
    response: String,
) = createSupabaseClient(
    supabaseKey = "",
    supabaseUrl = "",
) {
    install(Postgrest)
    httpEngine = fakeHttpEngine(status = status, response = response)
}
