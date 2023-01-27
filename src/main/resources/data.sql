INSERT INTO
    roles (id, name)
VALUES
    (1, 'ROLE_CANDIDATE'),
    (2, 'ROLE_RECRUITER'),
    (3, 'ROLE_MANAGER'),
    (4, 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO
    education_level (id, name)
VALUES
    (1, 'ENSINO_FUNDAMENTAL'),
    (2, 'ENSINO_MEDIO'),
    (3, 'GRADUACAO'),
    (4, 'POS_GRADUACAO'),
    (5, 'MESTRADO'),
    (6, 'DOUTORADO')
ON CONFLICT DO NOTHING;
--
--INSERT INTO users (id, email, password, username, created_at, updated_at, activated, cpf)
--VALUES (
--    nextval('users_id_seq'),
--    'admin@email.com',
--    '$2a$12$8FFHzw7x1oC7YCXiXL6.QekYh5WhKboJXeWT16hOfS5M2LOeEDR7u',
--    'admin',
--    NOW(),
--    NOW(),
--    true,
--    '08458465078')
--ON CONFLICT
--    DO NOTHING;
--
--INSERT INTO user_roles (user_id, role_id)
--VALUES
--    (1, 4)
--ON CONFLICT
--    DO NOTHING;