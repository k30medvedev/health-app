CREATE TABLE users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE analyses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    test_name       VARCHAR(255) NOT NULL,
    result_value    VARCHAR(100) NOT NULL,
    unit            VARCHAR(50),
    reference_range VARCHAR(100),
    taken_at        DATE         NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    CONSTRAINT fk_analyses_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_analyses_user_id ON analyses (user_id);
