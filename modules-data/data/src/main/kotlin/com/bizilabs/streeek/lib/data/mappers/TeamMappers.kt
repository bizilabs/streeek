package com.bizilabs.streeek.lib.data.mappers

import com.bizilabs.streeek.lib.data.mappers.team.toCache
import com.bizilabs.streeek.lib.data.mappers.team.toDomain
import com.bizilabs.streeek.lib.domain.helpers.DateFormats
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.UTCLocalDateTime
import com.bizilabs.streeek.lib.domain.helpers.asDate
import com.bizilabs.streeek.lib.domain.helpers.asString
import com.bizilabs.streeek.lib.domain.helpers.datetimeSystem
import com.bizilabs.streeek.lib.domain.helpers.datetimeUTC
import com.bizilabs.streeek.lib.domain.models.TeamDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDetailsDomain
import com.bizilabs.streeek.lib.domain.models.TeamDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberAccountDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberLevelDomain
import com.bizilabs.streeek.lib.domain.models.TeamMemberRole
import com.bizilabs.streeek.lib.domain.models.TeamWithMembersDomain
import com.bizilabs.streeek.lib.local.models.TeamCache
import com.bizilabs.streeek.lib.local.models.TeamDetailsCache
import com.bizilabs.streeek.lib.local.models.TeamMemberAccountCache
import com.bizilabs.streeek.lib.local.models.TeamMemberCache
import com.bizilabs.streeek.lib.local.models.TeamMemberLevelCache
import com.bizilabs.streeek.lib.remote.models.supabase.TeamDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamDetailsDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamMemberAccountDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamMemberDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamMemberLevelDTO
import com.bizilabs.streeek.lib.remote.models.supabase.TeamWithMembersDTO
import kotlinx.datetime.Instant

fun TeamDetailsCache.toDomain() = TeamDetailsDomain(
    team = team.toDomain(),
    page = page,
    members = members.map { it.toDomain() },
    rank = rank.toDomain(),
    top = top.mapValues { it.value.toDomain() }
)

fun TeamDetailsDomain.toCache() = TeamDetailsCache(
    team = team.toCache(),
    page = page,
    members = members.map { it.toCache() },
    rank = rank.toCache(),
    top = top.mapValues { it.value.toCache() }
)

fun TeamWithMembersDTO.toDomain() = TeamWithMembersDomain(
    members = members.map { it.toDomain() },
    team = team.toDomain(),
    details = current.toDomain()
)

fun TeamDetailsDTO.toDomain() = TeamMemberDetailsDomain(
    role = TeamMemberRole.valueOf(role.uppercase()),
    rank = rank
)

//<editor-fold desc="team">
fun TeamDTO.toDomain() = TeamDomain(
    id = id,
    name = name,
    public = is_public,
    createdAt = Instant.parse(created_at).datetimeUTC,
    count = count
)

fun TeamDomain.toCache() = TeamCache(
    id = id,
    name = name,
    isPublic = public,
    count = count,
    createdAt = createdAt.toString()
)

fun TeamCache.toDomain() = TeamDomain(
    id = id,
    name = name,
    public = isPublic,
    createdAt = createdAt.asDate(DateFormats.YYYY_MM_dd_T_HH_mm_ss_SSSSSS)?.datetimeUTC ?: UTCLocalDateTime,
    count = count
)
//</editor-fold>

//<editor-fold desc="team member">
fun TeamMemberDTO.toDomain() = TeamMemberDomain(
    account = account.toDomain(),
    level = level.toDomain(),
    points = points,
    rank = rank
)

fun TeamMemberDomain.toCache() = TeamMemberCache(
    account = account.toCache(),
    level = level.toCache(),
    points = points,
    rank = rank
)

fun TeamMemberCache.toDomain() = TeamMemberDomain(
    account = account.toDomain(),
    level = level.toDomain(),
    points = points,
    rank = rank
)
//</editor-fold>

//<editor-fold desc="team account">
fun TeamMemberAccountDTO.toDomain() = TeamMemberAccountDomain(
    avatarUrl = avatar_url,
    createdAt = created_at.asDate(DateFormats.YYYY_MM_ddTHH_mm_ss)?.datetimeSystem
        ?: SystemLocalDateTime,
    id = id,
    role = TeamMemberRole.valueOf(role.uppercase()),
    username = username
)

fun TeamMemberAccountDomain.toCache() = TeamMemberAccountCache(
    avatarUrl = avatarUrl,
    createdAt = createdAt.asString(DateFormats.YYYY_MM_ddTHH_mm_ss) ?: "",
    id = id,
    role = role.name,
    username = username
)

fun TeamMemberAccountCache.toDomain() = TeamMemberAccountDomain(
    avatarUrl = avatarUrl,
    createdAt = createdAt.asDate(DateFormats.YYYY_MM_ddTHH_mm_ss)?.datetimeSystem
        ?: SystemLocalDateTime,
    id = id,
    role = TeamMemberRole.valueOf(role),
    username = username
)
//</editor-fold>

//<editor-fold desc="team level">
fun TeamMemberLevelDTO.toDomain() = TeamMemberLevelDomain(
    id = id,
    name = level_name,
    number = level_number,
    maxPoints = max_points,
    minPoints = min_points
)

fun TeamMemberLevelDomain.toCache() = TeamMemberLevelCache(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints
)

fun TeamMemberLevelCache.toDomain() = TeamMemberLevelDomain(
    id = id,
    name = name,
    number = number,
    maxPoints = maxPoints,
    minPoints = minPoints
)
//</editor-fold>
