-- Materialized view for account streaks
DROP MATERIALIZED VIEW IF EXISTS public.account_streaks;
CREATE MATERIALIZED VIEW public.account_streaks AS
WITH distinct_dates AS (
    SELECT
        account_id,
        github_event_date AS event_date
    FROM contributions
    GROUP BY account_id, github_event_date
),
ranked_dates AS (
    SELECT
        account_id,
        event_date,
        ROW_NUMBER() OVER (PARTITION BY account_id ORDER BY event_date) AS rn
    FROM distinct_dates
),
streaks AS (
    SELECT
        account_id,
        COUNT(*) AS streak_length,
        MAX(event_date) AS last_date
    FROM (
        SELECT
            account_id,
            event_date,
            event_date - (rn || ' days')::interval AS adjusted_date
        FROM ranked_dates
    ) AS adjusted_dates
    GROUP BY account_id, adjusted_date
),
current_streaks AS (
    SELECT
        account_id,
        COUNT(*) AS current_streak
    FROM (
        SELECT
            account_id,
            event_date,
            event_date - (rn || ' days')::interval AS adjusted_date
        FROM ranked_dates
    ) AS adjusted_dates
    WHERE adjusted_date = (
        SELECT MAX(adjusted_date)
        FROM (
            SELECT
                account_id,
                event_date,
                event_date - (rn || ' days')::interval AS adjusted_date
            FROM ranked_dates
        ) sub
        WHERE sub.account_id = adjusted_dates.account_id
    )
    GROUP BY account_id
)
SELECT
    s.account_id,
    COALESCE(c.current_streak, 0) AS current_streak,
    MAX(s.streak_length) AS longest_streak,
    CURRENT_TIMESTAMP AS last_updated
FROM streaks s
LEFT JOIN current_streaks c ON s.account_id = c.account_id
GROUP BY s.account_id, c.current_streak;

-- Materialized view for account points
DROP MATERIALIZED VIEW IF EXISTS public.account_points;
CREATE MATERIALIZED VIEW public.account_points AS
SELECT
    account_id,
    SUM(points) AS total_points
FROM
    public.contributions
GROUP BY
    account_id;