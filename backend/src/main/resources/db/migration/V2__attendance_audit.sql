CREATE TABLE IF NOT EXISTS attendance_audit (
    id BIGSERIAL PRIMARY KEY,
    attendance_id BIGINT NOT NULL REFERENCES attendance(id),
    changed_field VARCHAR(32) NOT NULL,
    old_value VARCHAR(40),
    new_value VARCHAR(40) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    actor_user_id BIGINT NOT NULL,
    actor_email VARCHAR(190) NOT NULL,
    actor_name VARCHAR(255) NOT NULL,
    changed_at TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_attendance_audit_attendance_changed
    ON attendance_audit(attendance_id, changed_at DESC);
