-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    firebase_uid VARCHAR(255),
    display_name VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Instruments table
CREATE TABLE instruments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(100)
);

-- Genres table
CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(100)
);

-- Seed instruments
INSERT INTO instruments (name, icon) VALUES
    ('Guitar', 'guitar'),
    ('Bass', 'bass'),
    ('Drums', 'drums'),
    ('Vocals', 'vocals'),
    ('Piano', 'piano'),
    ('Keyboard', 'keyboard'),
    ('Saxophone', 'saxophone'),
    ('Trumpet', 'trumpet'),
    ('Violin', 'violin'),
    ('Cello', 'cello'),
    ('Flute', 'flute'),
    ('Harmonica', 'harmonica'),
    ('Ukulele', 'ukulele'),
    ('Banjo', 'banjo'),
    ('Mandolin', 'mandolin'),
    ('Synthesizer', 'synthesizer'),
    ('DJ/Turntables', 'turntables'),
    ('Percussion', 'percussion'),
    ('Trombone', 'trombone'),
    ('Clarinet', 'clarinet');

-- Seed genres
INSERT INTO genres (name, icon) VALUES
    ('Rock', 'rock'),
    ('Jazz', 'jazz'),
    ('Blues', 'blues'),
    ('Hip-Hop', 'hiphop'),
    ('R&B', 'rnb'),
    ('Pop', 'pop'),
    ('Country', 'country'),
    ('Folk', 'folk'),
    ('Metal', 'metal'),
    ('Punk', 'punk'),
    ('Electronic', 'electronic'),
    ('Classical', 'classical'),
    ('Reggae', 'reggae'),
    ('Latin', 'latin'),
    ('Funk', 'funk'),
    ('Soul', 'soul'),
    ('Indie', 'indie'),
    ('Alternative', 'alternative'),
    ('World', 'world'),
    ('Gospel', 'gospel');

-- Musician profiles table
CREATE TABLE musician_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    display_name VARCHAR(100),
    bio TEXT,
    skill_level VARCHAR(20),
    availability VARCHAR(500),
    photo_url VARCHAR(500),
    audio_intro_url VARCHAR(500),
    location GEOGRAPHY(Point, 4326),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    max_distance_miles INT DEFAULT 25,
    soundcloud_url VARCHAR(500),
    spotify_url VARCHAR(500),
    youtube_url VARCHAR(500),
    bandcamp_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Profile-instruments join table
CREATE TABLE profile_instruments (
    profile_id UUID NOT NULL REFERENCES musician_profiles(id) ON DELETE CASCADE,
    instrument_id INT NOT NULL REFERENCES instruments(id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, instrument_id)
);

-- Profile-genres join table
CREATE TABLE profile_genres (
    profile_id UUID NOT NULL REFERENCES musician_profiles(id) ON DELETE CASCADE,
    genre_id INT NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (profile_id, genre_id)
);

-- Swipe history table
CREATE TABLE swipe_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    swiper_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    swiped_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    direction VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (swiper_id, swiped_id)
);

-- Matches table
CREATE TABLE matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user1_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    matched_at TIMESTAMP NOT NULL DEFAULT now(),
    is_active BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (user1_id, user2_id)
);

-- Bands table
CREATE TABLE bands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(100),
    bio TEXT,
    photo_url VARCHAR(500),
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Band members table
CREATE TABLE band_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    band_id UUID NOT NULL REFERENCES bands(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(100),
    joined_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (band_id, user_id)
);

-- Jam events table
CREATE TABLE jam_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location_name VARCHAR(255),
    location GEOGRAPHY(Point, 4326),
    event_date TIMESTAMP NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    band_id UUID REFERENCES bands(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Jam event attendees table
CREATE TABLE jam_event_attendees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL REFERENCES jam_events(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    responded_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (event_id, user_id)
);

-- Spatial index on musician_profiles location
CREATE INDEX idx_musician_profiles_location ON musician_profiles USING GIST (location);

-- Swipe history indexes
CREATE INDEX idx_swipe_history_swiper_swiped ON swipe_history (swiper_id, swiped_id);

-- Matches indexes
CREATE INDEX idx_matches_user1 ON matches (user1_id);
CREATE INDEX idx_matches_user2 ON matches (user2_id);
