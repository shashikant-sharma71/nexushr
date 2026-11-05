ALTER TABLE notifications ADD COLUMN IF NOT EXISTS announcement_id BIGINT REFERENCES announcements(id) ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS idx_notifications_user_unread ON notifications(user_id, read);
CREATE INDEX IF NOT EXISTS idx_notifications_announcement ON notifications(announcement_id);
