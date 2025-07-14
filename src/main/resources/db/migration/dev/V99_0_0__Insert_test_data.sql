INSERT INTO users (id, username, password, first_name, last_name, enabled)
VALUES ('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'test.user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqR2e5dOKJgiMf4n.vHYv-wMjwGu', 'Test', 'User', true);

INSERT INTO authorities (username, authority) VALUES ('test.user', 'ROLE_USER');

INSERT INTO accounts (id, user_id, balance_pln, balance_usd, version)
VALUES ('f0e9d8c7-b6a5-4321-fedc-ba9876543210', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 5000.00, 0, 0);