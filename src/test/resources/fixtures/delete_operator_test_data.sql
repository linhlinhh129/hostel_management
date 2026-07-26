-- Delete data from child to parent to avoid foreign key constraints
DELETE FROM dbo.post_comments;
DELETE FROM dbo.post_reactions;
DELETE FROM dbo.community_posts;
DELETE FROM dbo.contracts;
DELETE FROM dbo.audit_logs;
DELETE FROM dbo.notifications;
DELETE FROM dbo.requests;
DELETE FROM dbo.payments;
DELETE FROM dbo.invoices;
DELETE FROM dbo.meter_readings;
DELETE FROM dbo.dependents;
DELETE FROM dbo.rooms;
DELETE FROM dbo.facilities;
DELETE FROM dbo.users WHERE username LIKE 'test_%';
