package com.bizilabs.streeek.lib.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bizilabs.streeek.lib.remote.helpers.createHttpClient
import com.bizilabs.streeek.lib.remote.helpers.createSupabase
import com.bizilabs.streeek.lib.remote.interceptor.AuthorizationInterceptor
import com.bizilabs.streeek.lib.remote.interceptor.NetworkInterceptor
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSource
import com.bizilabs.streeek.lib.remote.sources.account.AccountRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSource
import com.bizilabs.streeek.lib.remote.sources.auth.AuthenticationRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.contributions.ContributionsRemoteSource
import com.bizilabs.streeek.lib.remote.sources.contributions.ContributionsRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.issues.IssuesRemoteSource
import com.bizilabs.streeek.lib.remote.sources.issues.IssuesRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.labels.LabelRemoteSource
import com.bizilabs.streeek.lib.remote.sources.labels.LabelRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.leaderboard.LeaderboardRemoteSource
import com.bizilabs.streeek.lib.remote.sources.leaderboard.LeaderboardRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.level.LevelRemoteSource
import com.bizilabs.streeek.lib.remote.sources.level.LevelRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.notifications.NotificationRemoteSource
import com.bizilabs.streeek.lib.remote.sources.notifications.NotificationRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSource
import com.bizilabs.streeek.lib.remote.sources.preferences.RemotePreferencesSourceImpl
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSource
import com.bizilabs.streeek.lib.remote.sources.team.TeamRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamInvitationRemoteSource
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamInvitationRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.team.requests.TeamRequestRemoteSource
import com.bizilabs.streeek.lib.remote.sources.team.requests.TeamRequestRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSource
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSourceImpl
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.HttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "streeek.remote")

val RemoteModule =
    module {
        single<HttpLoggingInterceptor> {
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        }
        single<ChuckerInterceptor> { ChuckerInterceptor(context = get()) }
        single<AuthorizationInterceptor> { AuthorizationInterceptor(remotePreferencesSource = get()) }
        single<NetworkInterceptor> { NetworkInterceptor(context = get()) }
        single<HttpClient> {
            createHttpClient(
                networkInterceptor = get(),
                chuckerInterceptor = get(),
                loggingInterceptor = get(),
                authorizationInterceptor = get(),
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
                preferences = get(),
            )
        }
        single<UserRemoteSource> { UserRemoteSourceImpl(client = get()) }
        single<AccountRemoteSource> {
            AccountRemoteSourceImpl(
                supabase = get(),
                remotePreferencesSource = get(),
            )
        }
        single<ContributionsRemoteSource> {
            ContributionsRemoteSourceImpl(
                supabase = get(),
                client = get(),
            )
        }
        single<TeamRemoteSource> { TeamRemoteSourceImpl(supabase = get()) }
        single<TeamInvitationRemoteSource> { TeamInvitationRemoteSourceImpl(supabase = get()) }
        single<LevelRemoteSource> { LevelRemoteSourceImpl(supabase = get()) }
        single<NotificationRemoteSource> { NotificationRemoteSourceImpl(supabase = get()) }
        single<IssuesRemoteSource> { IssuesRemoteSourceImpl(client = get()) }
        single<LabelRemoteSource> { LabelRemoteSourceImpl(client = get()) }
        single<LeaderboardRemoteSource> { LeaderboardRemoteSourceImpl(supabase = get()) }
        single<TeamRequestRemoteSource> { TeamRequestRemoteSourceImpl(supabase = get()) }
    }
