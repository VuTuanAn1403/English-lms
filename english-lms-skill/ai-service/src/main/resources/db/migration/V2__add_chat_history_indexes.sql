-- Migration V2: Performance Optimization Indexes for AI Chat Histories

CREATE INDEX IF NOT EXISTS idx_chat_histories_user_session ON chat_histories (user_id, session_id);
CREATE INDEX IF NOT EXISTS idx_chat_histories_lookup ON chat_histories (user_id, session_id, prompt_type, created_at);
