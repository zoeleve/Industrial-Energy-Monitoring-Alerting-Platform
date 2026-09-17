CREATE TABLE measurements (
    id                  BIGSERIAL PRIMARY KEY,
    device_id           BIGINT NOT NULL REFERENCES devices(id),
    recorded_at         TIMESTAMP        NOT NULL,
    energy_consumption  DOUBLE PRECISION NOT NULL,
    power               DOUBLE PRECISION NOT NULL,
    voltage             DOUBLE PRECISION NOT NULL,
    created_at          TIMESTAMP        NOT NULL DEFAULT now()
);

CREATE INDEX idx_measurements_device_id ON measurements(device_id);
