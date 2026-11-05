ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;
-- Leave status is stored as VARCHAR, so MANAGER_APPROVED needs no table alteration.
CREATE INDEX IF NOT EXISTS idx_employees_manager_id ON employees(manager_id);
CREATE INDEX IF NOT EXISTS idx_users_role_active ON users(role, active);

CREATE TABLE IF NOT EXISTS admin_audit_logs (id BIGSERIAL PRIMARY KEY, actor_email VARCHAR(255) NOT NULL, action VARCHAR(100) NOT NULL, target_user_id BIGINT NOT NULL, details VARCHAR(1000), created_at TIMESTAMP NOT NULL);
CREATE INDEX IF NOT EXISTS idx_admin_audit_created_at ON admin_audit_logs(created_at);
