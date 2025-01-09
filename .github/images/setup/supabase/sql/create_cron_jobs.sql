
SELECT cron.schedule('*/5 * * * *', 'REFRESH MATERIALIZED VIEW public.account_streaks;');
SELECT cron.schedule('*/5 * * * *', 'REFRESH MATERIALIZED VIEW public.account_points;');
SELECT cron.schedule('0 * * * *', 'SELECT delete_expired_invitations();');
