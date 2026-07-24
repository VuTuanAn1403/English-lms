-- V2: Insert sample courses and lessons
INSERT INTO courses (id, title, description, level, image_url, created_at, updated_at)
VALUES 
    (
        '550e8400-e29b-41d4-a716-446655440001',
        'English Communication - Giao Tiếp Căn Bản',
        'Khóa học giúp học viên tự tin giao tiếp tiếng Anh trong các tình huống hàng ngày với trợ lý AI hỗ trợ 24/7.',
        'Cơ bản',
        'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?auto=format&fit=crop&w=600&q=80',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '550e8400-e29b-41d4-a716-446655440002',
        'IELTS Foundation 5.5+',
        'Lộ trình luyện thi IELTS từ cơ bản đến 5.5+ đầy đủ 4 kỹ năng Nghe, Nói, Đọc, Viết kèm trắc nghiệm tự động.',
        'Trung cấp',
        'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=600&q=80',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '550e8400-e29b-41d4-a716-446655440003',
        'Business English - Tiếng Anh Thương Mại',
        'Kỹ năng viết Email công việc, thuyết trình và đàm phán bằng tiếng Anh chuyên nghiệp cho người đi làm.',
        'Nâng cao',
        'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=600&q=80',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO lessons (id, course_id, title, content, video_url, pdf_url, lesson_order, created_at)
VALUES
    (
        '550e8400-e29b-41d4-a716-446655441001',
        '550e8400-e29b-41d4-a716-446655440001',
        'Bài 1: Present Simple - Thì Hiện Tại Đơn',
        'Thì hiện tại đơn diễn tả hành động lặp đi lặp lại hoặc sự thật hiển nhiên. Cấu trúc: S + V(s/es).',
        'https://www.youtube.com/embed/dQw4w9WgXcQ',
        'https://pdf.example.com/lesson1.pdf',
        1,
        CURRENT_TIMESTAMP
    ),
    (
        '550e8400-e29b-41d4-a716-446655441002',
        '550e8400-e29b-41d4-a716-446655440001',
        'Bài 2: Present Continuous - Thì Hiện Tại Tiếp Diễn',
        'Diễn tả hành động đang diễn ra tại thời điểm nói hoặc kế hoạch tương lai gần. Cấu trúc: S + am/is/are + V-ing.',
        'https://www.youtube.com/embed/dQw4w9WgXcQ',
        NULL,
        2,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (id) DO NOTHING;
