package com.bizilabs.streeek.lib.data

import com.bizilabs.streeek.lib.data.repositories.AuthenticationRepositoryImpl
import com.bizilabs.streeek.lib.data.repositories.UserRepositoryImpl
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.domain.repositories.UserRepository
import com.bizilabs.streeek.lib.remote.remoteModule
import org.koin.dsl.module

val dataModule = module {
    includes(remoteModule)
    single<AuthenticationRepository> { AuthenticationRepositoryImpl(remote = get()) }
    single<UserRepository> { UserRepositoryImpl(remote = get()) }
}
