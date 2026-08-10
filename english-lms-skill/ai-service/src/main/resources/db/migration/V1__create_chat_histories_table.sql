-- V1: Create chat_histories table in ai_db
CREATE TABLE IF NOT EXISTS chat_histories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    user_email VARCHAR(100),
    session_id VARCHAR(100),
    prompt_type VARCHAR(20) NOT NULL,
    user_prompt TEXT,
    ai_response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chat_histories_user_email ON chat_histories(user_email);
CREATE INDEX IF NOT EXISTS idx_chat_histories_session_id ON chat_histories(session_id);
CREATE INDEX IF NOT EXISTS idx_chat_histories_prompt_type ON chat_histories(prompt_type);
