CREATE TABLE devices (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(100) NOT NULL,
    location    VARCHAR(255) NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);
