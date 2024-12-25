package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberAccountDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberLevelDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.remote.models.supabase.TeamDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamDetailsDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamMemberAccountDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamMemberDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamMemberLevelDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamWithMembersDTO
import kotlinx.datetime.Instant

fun TeamWithMembersDTO.toDomain() = TeamWithMembersDomain(
    members = members.map { it.toDomain() },
    team = team.toDomain(),
    details = current.toDomain()
)

fun TeamDetailsDTO.toDomain() = TeamDetailsDomain(
    role = TeamMemberRole.valueOf(role.uppercase()),
    rank = rank
)

fun TeamDTO.toDomain() = TeamDomain(
    id = id,
    name = name,
    public = is_public,
    createdAt = Instant.parse(created_at).datetimeUTC,
    count = count
)

fun TeamMemberDTO.toDomain() = TeamMemberDomain(
    account = account.toDomain(),
    level = level.toDomain(),
    points = points,
    rank = rank
)

fun TeamMemberAccountDTO.toDomain() = TeamMemberAccountDomain(
    avatarUrl = avatar_url,
    createdAt = created_at.asDate(DateFormats.YYYY_MM_ddTHH_mm_ss)?.datetimeSystem
        ?: SystemLocalDateTime,
    id = id,
    role = TeamMemberRole.valueOf(role.uppercase()),
    username = username
)

fun TeamMemberLevelDTO.toDomain() = TeamMemberLevelDomain(
    id = id,
    name = level_name,
    number = level_number,
    maxPoints = max_points,
    minPoints = min_points
)
