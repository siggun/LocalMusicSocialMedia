-- Bands
CREATE TABLE bands (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    genre       VARCHAR(50),
    bio         TEXT,
    photo_url   VARCHAR(500),
    created_by  UUID          NOT NULL REFERENCES users(id),
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE band_members (
    id        UUID PRIMARY KEY,
    band_id   UUID        NOT NULL REFERENCES bands(id) ON DELETE CASCADE,
    user_id   UUID        NOT NULL REFERENCES users(id),
    role      VARCHAR(50),
    joined_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (band_id, user_id)
);

CREATE INDEX idx_band_members_user ON band_members(user_id);
CREATE INDEX idx_band_members_band ON band_members(band_id);

-- Jam Events
CREATE TABLE jam_events (
    id            UUID PRIMARY KEY,
    title         VARCHAR(200)  NOT NULL,
    description   TEXT,
    location_name VARCHAR(200),
    latitude      DOUBLE PRECISION,
    longitude     DOUBLE PRECISION,
    event_date    TIMESTAMP     NOT NULL,
    created_by    UUID          NOT NULL REFERENCES users(id),
    band_id       UUID          REFERENCES bands(id),
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE jam_event_attendees (
    id           UUID PRIMARY KEY,
    event_id     UUID        NOT NULL REFERENCES jam_events(id) ON DELETE CASCADE,
    user_id      UUID        NOT NULL REFERENCES users(id),
    status       VARCHAR(20) NOT NULL,
    responded_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (event_id, user_id)
);

CREATE INDEX idx_jam_events_date     ON jam_events(event_date);
CREATE INDEX idx_jam_events_creator  ON jam_events(created_by);
CREATE INDEX idx_jam_attendees_user  ON jam_event_attendees(user_id);
CREATE INDEX idx_jam_attendees_event ON jam_event_attendees(event_id);
