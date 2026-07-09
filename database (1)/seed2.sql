-- ============================================================
-- SAMPLE DATA - COMMUNITY POSTS
-- ============================================================

IF NOT EXISTS (SELECT 1 FROM dbo.community_posts WHERE title = N'Tìm chủ xe máy')
BEGIN
INSERT INTO dbo.community_posts
(title, content, image_url, author_id, status, reviewed_by)
VALUES
(
    N'Tìm chủ xe máy',
    N'Có một xe máy Honda Vision màu trắng để quên trước khu A từ tối qua. Chủ xe vui lòng liên hệ Ban quản lý.',
    N'/uploads/community/post1.jpg',
    (SELECT user_id FROM dbo.users WHERE username = N'lethithuylinhtl12@gmail.com'),
    N'APPROVED',
    (SELECT user_id FROM dbo.users WHERE username = N'mn03112005@gmail.com')
);
END

IF NOT EXISTS (SELECT 1 FROM dbo.community_posts WHERE title = N'Nhặt được ví')
BEGIN
INSERT INTO dbo.community_posts
(title, content, image_url, author_id, status, reviewed_by)
VALUES
(
    N'Nhặt được ví',
    N'Tôi nhặt được một chiếc ví màu đen tại tầng hầm B1. Ai bị mất vui lòng liên hệ.',
    N'/uploads/community/post2.jpg',
    (SELECT user_id FROM dbo.users WHERE username = N'dov62995@gmail.com'),
    N'PENDING',
    NULL
);
END

IF NOT EXISTS (SELECT 1 FROM dbo.community_posts WHERE title = N'Cảnh báo lừa đảo')
BEGIN
INSERT INTO dbo.community_posts
(title, content, image_url, author_id, status, reviewed_by)
VALUES
(
    N'Cảnh báo lừa đảo',
    N'Có đối tượng giả danh nhân viên điện lực để thu tiền. Mọi người cần cảnh giác.',
    N'/uploads/community/post3.jpg',
    (SELECT user_id FROM dbo.users WHERE username = N'tenant03@gmail.com'),
    N'APPROVED',
    (SELECT user_id FROM dbo.users WHERE username = N'buidinhyt@gmail.com')
);
END

IF NOT EXISTS (SELECT 1 FROM dbo.community_posts WHERE title = N'Mất mèo')
BEGIN
INSERT INTO dbo.community_posts
(title, content, image_url, author_id, status, reviewed_by)
VALUES
(
    N'Mất mèo',
    N'Mèo Anh lông ngắn màu vàng bị thất lạc tại khu B. Ai nhìn thấy vui lòng liên hệ giúp.',
    N'/uploads/community/post4.jpg',
    (SELECT user_id FROM dbo.users WHERE username = N'tenant04@gmail.com'),
    N'REJECTED',
    (SELECT user_id FROM dbo.users WHERE username = N'mn03112005@gmail.com')
);
END

IF NOT EXISTS (SELECT 1 FROM dbo.community_posts WHERE title = N'Rủ chơi cầu lông')
BEGIN
INSERT INTO dbo.community_posts
(title, content, image_url, author_id, status, reviewed_by)
VALUES
(
    N'Rủ chơi cầu lông',
    N'Mình tìm thêm 2 bạn chơi cầu lông vào các tối thứ 3 và thứ 5.',
    NULL,
    (SELECT user_id FROM dbo.users WHERE username = N'tenant05@gmail.com'),
    N'APPROVED',
    (SELECT user_id FROM dbo.users WHERE username = N'buidinhyt@gmail.com')
);
END
-- ============================================================
-- SAMPLE DATA - POST REACTIONS
-- ============================================================

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT
    p.post_id,
    u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Tìm chủ xe máy'
AND u.username=N'dov62995@gmail.com'
AND NOT EXISTS(
    SELECT 1
    FROM dbo.post_reactions r
    WHERE r.post_id=p.post_id
    AND r.user_id=u.user_id
);

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT p.post_id,u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Tìm chủ xe máy'
AND u.username=N'tenant03@gmail.com'
AND NOT EXISTS(
SELECT 1 FROM dbo.post_reactions r
WHERE r.post_id=p.post_id
AND r.user_id=u.user_id);

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT p.post_id,u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Tìm chủ xe máy'
AND u.username=N'tenant04@gmail.com'
AND NOT EXISTS(
SELECT 1 FROM dbo.post_reactions r
WHERE r.post_id=p.post_id
AND r.user_id=u.user_id);

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT p.post_id,u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Cảnh báo lừa đảo'
AND u.username=N'lethithuylinhtl12@gmail.com'
AND NOT EXISTS(
SELECT 1 FROM dbo.post_reactions r
WHERE r.post_id=p.post_id
AND r.user_id=u.user_id);

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT p.post_id,u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Cảnh báo lừa đảo'
AND u.username=N'tenant05@gmail.com'
AND NOT EXISTS(
SELECT 1 FROM dbo.post_reactions r
WHERE r.post_id=p.post_id
AND r.user_id=u.user_id);

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT p.post_id,u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Rủ chơi cầu lông'
AND u.username=N'lethithuylinhtl12@gmail.com'
AND NOT EXISTS(
SELECT 1 FROM dbo.post_reactions r
WHERE r.post_id=p.post_id
AND r.user_id=u.user_id);

INSERT INTO dbo.post_reactions(post_id,user_id)
SELECT p.post_id,u.user_id
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Rủ chơi cầu lông'
AND u.username=N'tenant03@gmail.com'
AND NOT EXISTS(
SELECT 1 FROM dbo.post_reactions r
WHERE r.post_id=p.post_id
AND r.user_id=u.user_id);
-- ============================================================
-- SAMPLE DATA - POST COMMENTS
-- ============================================================

INSERT INTO dbo.post_comments(post_id,user_id,content)
SELECT
    p.post_id,
    u.user_id,
    N'Mình cũng thấy chiếc xe này từ tối hôm qua.'
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Tìm chủ xe máy'
AND u.username=N'dov62995@gmail.com';

INSERT INTO dbo.post_comments(post_id,user_id,content)
SELECT
    p.post_id,
    u.user_id,
    N'Hy vọng chủ xe sớm nhận lại.'
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Tìm chủ xe máy'
AND u.username=N'tenant03@gmail.com';

INSERT INTO dbo.post_comments(post_id,user_id,content)
SELECT
    p.post_id,
    u.user_id,
    N'Cảm ơn bạn đã chia sẻ thông tin.'
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Cảnh báo lừa đảo'
AND u.username=N'lethithuylinhtl12@gmail.com';

INSERT INTO dbo.post_comments(post_id,user_id,content)
SELECT
    p.post_id,
    u.user_id,
    N'Mình cũng từng gặp trường hợp tương tự.'
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Cảnh báo lừa đảo'
AND u.username=N'tenant04@gmail.com';

INSERT INTO dbo.post_comments(post_id,user_id,content)
SELECT
    p.post_id,
    u.user_id,
    N'Mình tham gia nhé.'
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Rủ chơi cầu lông'
AND u.username=N'lethithuylinhtl12@gmail.com';

INSERT INTO dbo.post_comments(post_id,user_id,content)
SELECT
    p.post_id,
    u.user_id,
    N'Cho mình xin thời gian cụ thể với.'
FROM dbo.community_posts p
CROSS JOIN dbo.users u
WHERE p.title=N'Rủ chơi cầu lông'
AND u.username=N'dov62995@gmail.com';

