-- Roles
INSERT INTO roles (role_name)
VALUES ('ROLE_USER')
ON CONFLICT (role_name) DO NOTHING;

INSERT INTO roles (role_name)
VALUES ('ROLE_ADMIN')
ON CONFLICT (role_name) DO NOTHING;

-- Admin user (password = admin)
INSERT INTO users (username, email, password, location, phone_number)
VALUES (
    'Admin',
    'admin@example.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi7nXz6xXxV9I9B1wFqk7RZ4p8zG9lS',
    'GUJARAT',
    '9999999999'
);

-- User
INSERT INTO users (username, email, password, location, phone_number)
VALUES (
    'User',
    'user@example.com',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi7nXz6xXxV9I9B1wFqk7RZ4p8zG9lS',
    'GUJARAT',
    '8888888888'
);

-- Role mapping
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.role_id FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.role_name = 'ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.role_id FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.role_name = 'ROLE_USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.role_id FROM users u, roles r
WHERE u.email = 'user@example.com' AND r.role_name = 'ROLE_USER';