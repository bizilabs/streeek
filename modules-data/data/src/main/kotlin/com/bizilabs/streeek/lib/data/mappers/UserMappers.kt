package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.models.UserDomain
import com.bizilabs.streeek.lib.remote.models.GithubUserDTO

fun GithubUserDTO.toDomain() = UserDomain(id = id, name = name, email = email, bio = bio, url = url)
