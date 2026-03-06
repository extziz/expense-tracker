CREATE TABLE users (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,      -- BCrypt hashes are always 60 chars, but 255 is safe
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Add user ownership to existing expenses
-- nullable initially so existing rows don't violate the constraint
ALTER TABLE expenses
    ADD COLUMN user_id BIGINT REFERENCES users(id);