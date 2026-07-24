-- V2: Insert seed admin and student users
-- Password for both accounts is "123456"
INSERT INTO users (id, full_name, email, password, role, avatar, created_at, updated_at)
VALUES 
    (
        '550e8400-e29b-41d4-a716-446655440000',
        'Administrator',
        'admin@gmail.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1Mv.W/S/N04u',
        'ADMIN',
        'https://ui-avatars.com/api/?name=Admin',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '550e8400-e29b-41d4-a716-446655440001',
        'Nguyễn Văn A',
        'student@gmail.com',
        '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xD0m1Mv.W/S/N04u',
        'STUDENT',
        'https://ui-avatars.com/api/?name=Student',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (email) DO NOTHING;
