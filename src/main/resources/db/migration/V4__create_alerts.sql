CREATE TABLE alert_rules (
    id          BIGSERIAL PRIMARY KEY,
    device_id   BIGINT NOT NULL REFERENCES devices(id),
    metric      VARCHAR(50) NOT NULL,
    operator    VARCHAR(20) NOT NULL,
    threshold   DOUBLE PRECISION NOT NULL,
    alert_type  VARCHAR(100) NOT NULL,
    enabled     BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_alert_rules_device_id ON alert_rules(device_id);

CREATE TABLE alerts (
    id                BIGSERIAL PRIMARY KEY,
    device_id         BIGINT NOT NULL REFERENCES devices(id),
    rule_id           BIGINT NOT NULL REFERENCES alert_rules(id),
    triggering_value  DOUBLE PRECISION NOT NULL,
    status            VARCHAR(20) NOT NULL,
    triggered_at      TIMESTAMP NOT NULL,
    resolved_at       TIMESTAMP
);

CREATE INDEX idx_alerts_device_id ON alerts(device_id);
CREATE INDEX idx_alerts_status ON alerts(status);
