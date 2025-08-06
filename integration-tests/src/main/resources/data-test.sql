-- Test data insertion
INSERT INTO test_user (username, email) VALUES
    ('testuser1', 'test1@example.com'),
    ('testuser2', 'test2@example.com'),
    ('testuser3', 'test3@example.com');

INSERT INTO test_item (name, description, user_id) VALUES
    ('Test Item 1', 'Description for test item 1', 1),
    ('Test Item 2', 'Description for test item 2', 1),
    ('Test Item 3', 'Description for test item 3', 2),
    ('Test Item 4', 'Description for test item 4', 3);