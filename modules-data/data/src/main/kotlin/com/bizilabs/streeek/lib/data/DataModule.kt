package com.bizilabs.streeek.lib.data

import com.bizilabs.streeek.lib.data.repositories.AccountRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.AuthenticationRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.ContributionRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.PreferenceRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.TeamRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.UserRepositoryImpl
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.domain.repositories.ContributionRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.domain.repositories.TeamRepository
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.local.LocalModule
import com.bizilabs.streeek.lib.remote.RemoteModule
import org.koin.dsl.module

val dataModule = module {
    includes(RemoteModule, LocalModule)
    single<AuthenticationRepository> { AuthenticationRepositoryImpl(remote = get()) }
    single<UserRepository> { UserRepositoryImpl(remote = get(), accountLocalSource = get()) }
    single<AccountRepository> { AccountRepositoryImpl(remote = get(), local = get()) }
    single<ContributionRepository> {
        ContributionRepositoryImpl(
            local = get(),
            remote = get(),
            accountLocalSource = get()
        )
    }
    single<PreferenceRepository> {
        PreferenceRepositoryImpl(localSource = get(), remoteSource = get())
    }
    single<TeamRepository> {
        TeamRepositoryImpl(remoteSource = get(), accountLocalSource = get())
    }
}
