CREATE OR REPLACE FUNCTION get_account_with_points_and_level(value_account_id bigint)
RETURNS JSON AS $$
DECLARE
    result JSON;           -- To store the final JSON result
    total_points INTEGER;   -- To store the calculated total points
    level_record JSON;      -- To store the user level as an object from the levels table
    streak_record JSON;      -- To store the streak data from account_streaks
BEGIN
    -- Step 1: Calculate total points for the account
    SELECT COALESCE(SUM(ap.total_points), 0)
    INTO total_points
    FROM public.account_points ap
    WHERE ap.account_id = value_account_id;

    -- Step 2: Fetch the user level from the levels table based on total points
    SELECT to_jsonb(l)  -- Dynamically fetch all columns from the levels table
    INTO level_record
    FROM public.levels l
    WHERE total_points BETWEEN l.min_points AND l.max_points
    ORDER BY l.min_points DESC
    LIMIT 1;  -- Get the highest matching level

    -- Step 3: Fetch the streak data from account_streaks
    SELECT to_jsonb(s)  -- Dynamically fetch all columns from the account_streaks view
    INTO streak_record
    FROM public.account_streaks s
    WHERE s.account_id = value_account_id;

    -- Step 4: Build the JSON result
    SELECT json_build_object(
        'account', to_jsonb(a),  -- Dynamically fetch all columns from the accounts table
        'total_points', total_points,
        'level', level_record,
        'streak', streak_record  -- Append the streak data
    )
    INTO result
    FROM public.accounts a
    WHERE a.id = value_account_id;

    -- Step 5: Return the JSON result
    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_levels(p_page integer, p_page_size integer)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT json_agg(level_data) INTO result
    FROM (
        SELECT
            l.id,
            l.level_name,
            l.min_points,
            l.max_points,
            l.created_at,
            l.level_number
        FROM public.levels l
        ORDER BY l.level_number
        LIMIT p_page_size OFFSET (p_page - 1) * p_page_size
    ) AS level_data;

    RETURN COALESCE(result, '[]');  -- Return an empty JSON array if no levels are found
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_teams_for_account(p_account_id bigint)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT json_agg(json_build_object(
        'team', json_build_object(
            'id', t.id,
            'name', t.name,
            'is_public', t.is_public,
            'created_at', t.created_at,
            'updated_at', t.updated_at,
            'members_count', (SELECT COUNT(*) FROM public.team_members WHERE team_id = t.id)
        ),
        'member', json_build_object(
            'rank', member_rank.rank,  -- Use the calculated rank from the subquery
            'role', tm.role
        )
    )) INTO result
    FROM public.teams t
    JOIN public.team_members tm ON t.id = tm.team_id
    JOIN (
        SELECT
            tm_inner.team_id,
            tm_inner.account_id,
            RANK() OVER (PARTITION BY tm_inner.team_id ORDER BY COALESCE(ap.total_points, 0) DESC) AS rank
        FROM public.team_members tm_inner
        LEFT JOIN public.account_points ap ON tm_inner.account_id = ap.account_id
    ) AS member_rank ON tm.team_id = member_rank.team_id AND tm.account_id = member_rank.account_id
    WHERE tm.account_id = p_account_id;

    RETURN COALESCE(result, '[]');  -- Return an empty JSON array if no teams found
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION leave_team(
    p_account_id bigint,
    p_team_id bigint
)
RETURNS JSON AS $$
DECLARE
    result JSON;
    is_member boolean;
BEGIN
    -- Check if the account is a member of the team
    SELECT EXISTS(
        SELECT 1
        FROM public.team_members
        WHERE team_id = p_team_id AND account_id = p_account_id
    ) INTO is_member;

    IF NOT is_member THEN
        RAISE EXCEPTION 'Account with ID % is not a member of team %', p_account_id, p_team_id;
    END IF;

    -- Delete the account from the team_members table
    DELETE FROM public.team_members
    WHERE team_id = p_team_id AND account_id = p_account_id;

    -- Return a confirmation message
    result := json_build_object(
        'message', 'Account with ID ' || p_account_id || ' has left team ' || p_team_id || ' successfully.',
        'team_id', p_team_id,
        'account_id', p_account_id
    );

    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_team(
    p_team_id bigint,
    p_name text,
    p_is_public boolean,
    p_account_id bigint
)
RETURNS void AS $$
DECLARE
    user_role text;
BEGIN
    -- Check the user's role in the team
    SELECT role INTO user_role
    FROM public.team_members
    WHERE team_id = p_team_id AND account_id = p_account_id;

    -- Raise an exception if the user is not an owner or admin
    IF user_role IS NULL THEN
        RAISE EXCEPTION 'User is not a member of the team.';
    ELSIF user_role NOT IN ('owner', 'admin') THEN
        RAISE EXCEPTION 'User does not have permission to update the team.';
    END IF;

    -- Update the team details
    UPDATE public.teams
    SET name = p_name,
        is_public = p_is_public
    WHERE id = p_team_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_team_with_members_and_account(
    p_team_id bigint,
    p_account_id bigint,
    p_page integer,
    p_page_size integer
)
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    WITH ranked_members AS (
        SELECT
            tm.account_id AS member_account_id,
            tm.team_id,
            a.id AS account_id,
            a.username,
            a.email,
            a.created_at,
            a.avatar_url,
            tm.role,
            COALESCE(ap.total_points, 0) AS total_points,
            l.id AS level_id,
            l.level_name,
            l.min_points,
            l.max_points,
            l.level_number,
            RANK() OVER (PARTITION BY tm.team_id ORDER BY COALESCE(ap.total_points, 0) DESC) AS rank
        FROM
            public.team_members tm
        LEFT JOIN
            public.account_points ap ON tm.account_id = ap.account_id
        LEFT JOIN
            public.levels l ON COALESCE(ap.total_points, 0) BETWEEN l.min_points AND l.max_points
        LEFT JOIN
            public.accounts a ON tm.account_id = a.id
        WHERE
            tm.team_id = p_team_id
    ),
    current_member AS (
        SELECT
            rm.rank AS current_rank,
            rm.role AS current_role
        FROM
            ranked_members rm
        WHERE
            rm.account_id = p_account_id
        LIMIT 1 -- Ensure only one row is returned
    )
    SELECT
        json_build_object(
            'team', json_build_object(
                'id', t.id,
                'name', t.name,
                'is_public', t.is_public,
                'created_at', t.created_at,
                'members_count', COUNT(rm.member_account_id)
            ),
            'current', COALESCE((
                SELECT json_build_object(
                    'rank', cm.current_rank,
                    'role', cm.current_role
                )
                FROM current_member cm
            ), NULL),
            'members', json_agg(json_build_object(
                'account', json_build_object(
                    'id', rm.account_id,
                    'username', rm.username,
                    'email', rm.email,
                    'avatar_url', rm.avatar_url,
                    'created_at', rm.created_at,
                    'role', rm.role
                ),
                'points', rm.total_points,
                'rank', rm.rank,
                'level', json_build_object(
                    'id', rm.level_id,
                    'level_name', rm.level_name,
                    'min_points', rm.min_points,
                    'max_points', rm.max_points,
                    'level_number', rm.level_number
                )
            ) ORDER BY rm.rank ASC)
        )
    INTO result
    FROM
        ranked_members rm
    JOIN
        public.teams t ON rm.team_id = t.id
    WHERE
        t.id = p_team_id
    GROUP BY
        t.id, t.name, t.is_public, t.created_at
    LIMIT p_page_size OFFSET (p_page - 1) * p_page_size;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Function to Create a Team
CREATE OR REPLACE FUNCTION create_team(p_name text, p_is_public boolean, p_account_id bigint)
RETURNS bigint AS $$
DECLARE
    new_team_id bigint;
BEGIN
    INSERT INTO public.teams (name, is_public)
    VALUES (p_name, p_is_public)
    RETURNING id INTO new_team_id;

    -- Automatically add the creator as an admin
    INSERT INTO public.team_members (team_id, account_id, role)
    VALUES (new_team_id, p_account_id, 'owner');

    RETURN new_team_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION archive_team(p_team_id bigint, p_account_id bigint)
RETURNS void AS $$
DECLARE
    user_role text;
BEGIN
    -- Check the user's role in the team
    SELECT role INTO user_role
    FROM public.team_members
    WHERE team_id = p_team_id AND account_id = p_account_id;

    -- Raise an exception if the user is not an owner or admin
    IF user_role IS NULL THEN
        RAISE EXCEPTION 'User is not a member of the team.';
    ELSIF user_role NOT IN ('owner', 'admin') THEN
        RAISE EXCEPTION 'User does not have permission to archive the team.';
    END IF;

    -- Archive the team by setting the archived_at timestamp
    UPDATE public.teams
    SET archived_at = NOW()  -- Set the archived_at timestamp to the current time
    WHERE id = p_team_id
      AND archived_at IS NULL;  -- Ensure the team is not already archived
END;
$$ LANGUAGE plpgsql;

-- Function to Request to Join a Team
CREATE OR REPLACE FUNCTION join_team_request(p_team_id bigint, p_account_id bigint)
RETURNS void AS $$
BEGIN
    INSERT INTO public.team_requests (team_id, account_id, status)
    VALUES (p_team_id, p_account_id, 'pending');
END;
$$ LANGUAGE plpgsql;

-- Function to Accept or Reject Join Requests
CREATE OR REPLACE FUNCTION manage_team_request(p_request_id bigint, p_account_id bigint, p_status text)
RETURNS void AS $$
DECLARE
    user_role text;
    team_id bigint;
BEGIN
    -- Get the team ID associated with the request
    SELECT team_id INTO team_id
    FROM public.team_member_requests
    WHERE id = p_request_id;

    -- Check the user's role in the team
    SELECT role INTO user_role
    FROM public.team_members
    WHERE team_id = team_id AND account_id = p_account_id;

    -- Raise an exception if the user is not an owner or admin
    IF user_role IS NULL THEN
        RAISE EXCEPTION 'User is not a member of the team.';
    ELSIF user_role NOT IN ('owner', 'admin') THEN
        RAISE EXCEPTION 'User does not have permission to manage team requests.';
    END IF;

    -- Update the request status and set the resolved_at timestamp
    UPDATE public.team_member_requests
    SET status = p_status,
        resolved_at = NOW()  -- Set the resolved_at timestamp to the current time
    WHERE id = p_request_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_team_invite_code(
    p_team_id bigint,
    p_inviter_account_id bigint,
    p_expires_in integer DEFAULT 86400  -- Default expiration time in seconds (1 day)
)
RETURNS JSON AS $$
DECLARE
    result JSON;
    team_exists boolean;
    is_owner_or_admin boolean;
    invite_token char(6);  -- Rename token variable to invite_token
BEGIN
    -- Check if the team exists
    SELECT EXISTS(SELECT 1 FROM public.teams WHERE id = p_team_id) INTO team_exists;

    IF NOT team_exists THEN
        RAISE EXCEPTION 'Team with ID % does not exist', p_team_id;
    END IF;

    -- Check if the inviter is an owner or admin of the team
    SELECT EXISTS(
        SELECT 1
        FROM public.team_members
        WHERE team_id = p_team_id AND account_id = p_inviter_account_id AND role IN ('owner', 'admin')
    ) INTO is_owner_or_admin;

    IF NOT is_owner_or_admin THEN
        RAISE EXCEPTION 'Account with ID % is not authorized to invite members to team %', p_inviter_account_id, p_team_id;
    END IF;

    -- Generate a unique six-digit token
    LOOP
        invite_token := LPAD((FLOOR(RANDOM() * 1000000)::int)::text, 6, '0');  -- Generate a random six-digit code
        -- Check for uniqueness
        EXIT WHEN NOT EXISTS (SELECT 1 FROM public.team_invitations WHERE token = invite_token);
    END LOOP;

    -- Insert the invitation into the team_invitations table
    INSERT INTO public.team_invitations (team_id, creator_account_id, token, expires_at)
    VALUES (p_team_id, p_inviter_account_id, invite_token, NOW() + (p_expires_in * interval '1 second'))
    RETURNING json_build_object(
        'team_id', p_team_id,
        'token', invite_token,
        'expires_at', NOW() + (p_expires_in * interval '1 second')
    ) INTO result;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION join_team_with_invite_code(
    p_account_id bigint,
    p_token text
)
RETURNS JSON AS $$
DECLARE
    result JSON;
    invitation_team_id bigint;  -- Variable to hold the team ID from the invitation
BEGIN
    -- Check if the invitation exists and is valid (not expired)
    SELECT team_id INTO invitation_team_id
    FROM public.team_invitations
    WHERE token = p_token AND expires_at > NOW();

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Invalid or expired invitation token';
    END IF;

    -- Check if the account is already a member of the team
    IF EXISTS (
        SELECT 1
        FROM public.team_members
        WHERE team_id = invitation_team_id AND account_id = p_account_id
    ) THEN
        RAISE EXCEPTION 'Account with ID % is already a member of team %', p_account_id, invitation_team_id;
    END IF;

    -- Insert the account into the team_members table
    INSERT INTO public.team_members (team_id, account_id, role)
    VALUES (invitation_team_id, p_account_id, 'member')  -- Default role can be 'member'
    RETURNING json_build_object(
        'team_id', invitation_team_id,
        'account_id', p_account_id,
        'role', 'member'
    ) INTO result;

    -- Optionally, delete the invitation after accepting it
    --DELETE FROM public.team_invitations WHERE token = p_token;

    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_team_invitations(
    p_team_id bigint,
    p_account_id bigint
)
RETURNS JSON AS $$
DECLARE
    result JSON;
    is_owner_or_admin boolean;
BEGIN
    -- Check if the account is an owner or admin of the team
    SELECT EXISTS(
        SELECT 1
        FROM public.team_members
        WHERE team_id = p_team_id AND account_id = p_account_id AND role IN ('owner', 'admin')
    ) INTO is_owner_or_admin;

    IF NOT is_owner_or_admin THEN
        RAISE EXCEPTION 'Account with ID % is not authorized to view invitations for team %', p_account_id, p_team_id;
    END IF;

    -- Fetch all invitations for the specified team
    SELECT json_agg(json_build_object(
        'id', ti.id,
        'token', ti.token,
        'created_at', ti.created_at,
        'expires_at', ti.expires_at,
        'creator_account_id', ti.creator_account_id
    )) INTO result
    FROM public.team_invitations ti
    WHERE ti.team_id = p_team_id;

    RETURN COALESCE(result, '[]');  -- Return an empty JSON array if no invitations found
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_team_invitation(
    p_team_invitation_id bigint,
    p_account_id bigint
)
RETURNS JSON AS $$
DECLARE
    result JSON;
    invitation_team_id bigint;  -- Rename the variable to avoid ambiguity
    is_owner_or_admin boolean;
BEGIN
    -- Get the team ID associated with the invitation
    SELECT team_id INTO invitation_team_id
    FROM public.team_invitations
    WHERE id = p_team_invitation_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Invitation with ID % does not exist', p_team_invitation_id;
    END IF;

    -- Check if the account is an owner or admin of the team
    SELECT EXISTS(
        SELECT 1
        FROM public.team_members
        WHERE team_id = invitation_team_id AND account_id = p_account_id AND role IN ('owner', 'admin')
    ) INTO is_owner_or_admin;

    IF NOT is_owner_or_admin THEN
        RAISE EXCEPTION 'Account with ID % is not authorized to delete invitations for team %', p_account_id, invitation_team_id;
    END IF;

    -- Delete the invitation
    DELETE FROM public.team_invitations
    WHERE id = p_team_invitation_id;

    -- Return a confirmation message
    result := json_build_object(
        'message', 'Invitation with ID ' || p_team_invitation_id || ' has been deleted successfully.',
        'team_invitation_id', p_team_invitation_id
    );

    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_expired_invitations()
RETURNS void AS $$
BEGIN
    DELETE FROM public.team_invitations
    WHERE expires_at < NOW();  -- Delete invitations that have expired
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_notification(
    p_account_id BIGINT,
    p_title TEXT,
    p_message TEXT,
    p_payload TEXT
)
RETURNS JSON AS $$
DECLARE
    new_notification JSON;  -- Variable to hold the new notification as JSON
BEGIN
    INSERT INTO public.notifications (account_id, title, message, payload)
    VALUES (p_account_id, p_title, p_message, p_payload)
    RETURNING json_build_object(
        'id', id,
        'account_id', account_id,
        'title', title,
        'message', message,
        'payload', payload,
        'created_at', created_at,
        'read_at', read_at
    ) INTO new_notification;

    RETURN new_notification;  -- Return the inserted notification as JSON
END;
$$ LANGUAGE plpgsql;
