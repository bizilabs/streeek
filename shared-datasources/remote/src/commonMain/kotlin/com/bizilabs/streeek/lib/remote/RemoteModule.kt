package com.bizilabs.streeek.lib.remote

import com.bizilabs.streeek.lib.remote.helpers.DataStoreModule
import com.bizilabs.streeek.lib.remote.helpers.createHttpClient
import com.bizilabs.streeek.lib.remote.helpers.createSupabase
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
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamInvitationCodeRemoteSource
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamInvitationCodeRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamMemberInvitationRemoteSource
import com.bizilabs.streeek.lib.remote.sources.team.invitations.TeamMemberInvitationRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.team.requests.TeamRequestRemoteSource
import com.bizilabs.streeek.lib.remote.sources.team.requests.TeamRequestRemoteSourceImpl
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSource
import com.bizilabs.streeek.lib.remote.sources.user.UserRemoteSourceImpl
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val RemoteModule =
    module {
        includes(DataStoreModule)
        single<HttpClient> { createHttpClient(preferences = get()) }
        // settings
        single<ObservableSettings>(named("remote")) { Settings() as ObservableSettings }
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
            AccountRemoteSourceImpl(supabase = get(), remotePreferencesSource = get())
        }
        single<ContributionsRemoteSource> {
            ContributionsRemoteSourceImpl(supabase = get(), client = get())
        }
        single<TeamRemoteSource> { TeamRemoteSourceImpl(supabase = get()) }
        single<TeamInvitationCodeRemoteSource> { TeamInvitationCodeRemoteSourceImpl(supabase = get()) }
        single<LevelRemoteSource> { LevelRemoteSourceImpl(supabase = get()) }
        single<NotificationRemoteSource> { NotificationRemoteSourceImpl(supabase = get()) }
        single<IssuesRemoteSource> { IssuesRemoteSourceImpl(client = get()) }
        single<LabelRemoteSource> { LabelRemoteSourceImpl(client = get()) }
        single<LeaderboardRemoteSource> { LeaderboardRemoteSourceImpl(supabase = get()) }
        single<TeamRequestRemoteSource> { TeamRequestRemoteSourceImpl(supabase = get()) }
        single<TeamMemberInvitationRemoteSource> { TeamMemberInvitationRemoteSourceImpl(supabase = get()) }
    }
