package com.bizilabs.streeek.lib.data

import com.bizilabs.streeek.lib.data.repositories.AccountRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.AuthenticationRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.UserRepositoryImpl
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.local.LocalModule
import com.bizilabs.streeek.lib.remote.remoteModule
import org.koin.dsl.module

val dataModule = module {
    includes(remoteModule, LocalModule)
    single<AuthenticationRepository> { AuthenticationRepositoryImpl(remote = get()) }
    single<UserRepository> { UserRepositoryImpl(remote = get(), accountLocalSource = get()) }
    single<AccountRepository> { AccountRepositoryImpl(remote = get(), local = get()) }
}
