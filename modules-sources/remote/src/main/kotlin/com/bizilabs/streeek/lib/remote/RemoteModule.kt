package com.bizilabs.streeek.lib.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bizilabs.streeek.lib.remote.helpers.createHttpClient
import com.bizilabs.streeek.lib.remote.helpers.createSupabase
import com.bizilabs.streeek.lib.remote.interceptor.AuthorizationInterceptor
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSource
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSource
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.contributions.ContributionsRemoteSource
import com.bizilabs.streeek.lib.remote.sources.contributions.ContributionsRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSourceImpl
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSource
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSourceImpl
import io.ktor.client.HttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "streeek.remote")

val RemoteModule = module {
    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    single<AuthorizationInterceptor> { AuthorizationInterceptor(remotePreferencesSource = get()) }
    single<HttpClient> {
        createHttpClient(
            loggingInterceptor = get(),
            authorizationInterceptor = get()
        )
    }
    single<DataStore<Preferences>>(named("remote")) { get<Context>().dataStore }
    // supabase
    single { createSupabase() }
    // sources
    single<RemotePreferencesSource> { RemotePreferencesSourceImpl(dataStore = get(named("remote"))) }
    single<AuthenticationRemoteSource> {
        AuthenticationRemoteSourceImpl(
            client = get(),
            preferences = get()
        )
    }
    single<UserRemoteSource> { UserRemoteSourceImpl(client = get()) }
    single<AccountRemoteSource> { AccountRemoteSourceImpl(supabase = get()) }
    single<ContributionsRemoteSource> {
        ContributionsRemoteSourceImpl(
            supabase = get(),
            client = get()
        )
    }
}
