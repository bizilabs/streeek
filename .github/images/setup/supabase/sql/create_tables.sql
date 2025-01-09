-- Table to store user information
CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    github_id integer UNIQUE NOT NULL,
    username VARCHAR(255) unique not null,
    email VARCHAR(255),
    bio TEXT,
    url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contributions (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES accounts(id) ON DELETE CASCADE,
    github_event_id VARCHAR(255) UNIQUE NOT NULL,
    github_event_type VARCHAR(255) NOT NULL,
    github_event_date DATE NOT NULL,
    points INT NOT NULL,
    payload TEXT NOT NULL,
    repository TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.levels (
    id bigint primary key generated always as identity,
    level_name text not null,  -- Name of the level (e.g., "Beginner", "Intermediate", "Advanced")
    min_points integer not null,  -- Minimum points required for this level
    max_points integer not null,  -- Maximum points for this level
    created_at timestamp with time zone default current_timestamp,
    level_number integer not null,  -- Level number indicating the order of levels
    constraint new_levels_unique_level_name unique (level_name)
);

-- Create Teams Table
CREATE TABLE public.teams (
    id bigint primary key generated always as identity,
    name text not null,
    is_public boolean not null,
    created_at timestamp with time zone default now(),
    updated_at timestamp with time zone default now()
);

-- Create Archived Teams Table
CREATE TABLE public.archived_teams (
    id bigint primary key generated always as identity,
    team_id bigint references public.teams(id) on delete cascade,
    archived_at timestamp with time zone default now()
);

CREATE TABLE public.team_invitations (
    id bigint primary key generated always as identity,
    team_id bigint not null,
    creator_account_id bigint not null,  -- Store the account ID of the person creating the token
    token char(6) not null unique,  -- Change to char(6) for a six-digit code
    created_at timestamp with time zone default current_timestamp,
    expires_at timestamp with time zone,  -- Optional: to set an expiration for the invitation
    FOREIGN KEY (team_id) REFERENCES public.teams(id) ON DELETE CASCADE,
    FOREIGN KEY (creator_account_id) REFERENCES public.accounts(id) ON DELETE CASCADE  -- Assuming there is an accounts table
);

-- Create Team Members Table
CREATE TABLE public.team_members (
    id bigint primary key generated always as identity,
    team_id bigint references public.teams(id) on delete cascade,
    account_id bigint references public.accounts(id) on delete cascade,
    role text not null,  -- 'owner' or 'admin' or 'member'
    joined_at timestamp with time zone default now(),
    unique (team_id, account_id)  -- Prevent duplicate memberships
);

CREATE TABLE public.notifications (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,  -- Unique identifier for the notification
    account_id BIGINT NOT NULL,                          -- Reference to the account/user
    title TEXT NOT NULL,                                 -- Title of the notification
    message TEXT NOT NULL,                               -- Notification message
    payload TEXT,                                        -- Optional payload for additional data
    createdAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,  -- Creation timestamp
    readAt TIMESTAMP WITH TIME ZONE,                     -- Timestamp for when the notification was read
    FOREIGN KEY (account_id) REFERENCES public.accounts(id) ON DELETE CASCADE  -- Foreign key constraint
);

-- Create indexes for better performance
CREATE INDEX idx_account_id ON public.notifications(account_id);
CREATE INDEX idx_created_at ON public.notifications(createdAt);
