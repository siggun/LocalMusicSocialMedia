# BandSwipe – Stage 0: Planning Document

## 1. User Stories (MVP Scope)

### Authentication & Onboarding
| ID | Story | Priority |
|----|-------|----------|
| US-01 | As a new user, I can sign up with email/password so I can create an account | P0 |
| US-02 | As a new user, I can sign up with Google so onboarding is faster | P1 |
| US-03 | As a returning user, I can log in and be taken to the swipe deck | P0 |
| US-04 | As a new user, I am guided through a profile-creation wizard after sign-up | P0 |
| US-05 | As a user, I can reset my password via email | P1 |

### Musician Profile
| ID | Story | Priority |
|----|-------|----------|
| US-10 | As a musician, I can select multiple instruments I play (guitar, drums, vocals, etc.) | P0 |
| US-11 | As a musician, I can select multiple genres I'm into (rock, jazz, hip-hop, etc.) | P0 |
| US-12 | As a musician, I can set my skill level (beginner / intermediate / pro) | P0 |
| US-13 | As a musician, I can write a bio (max 500 chars) | P0 |
| US-14 | As a musician, I can upload a profile photo | P0 |
| US-15 | As a musician, I can record/upload a 15-second audio intro clip | P1 |
| US-16 | As a musician, I can add links to my SoundCloud/Spotify/YouTube/Bandcamp | P1 |
| US-17 | As a musician, I can set my availability (weekday evenings, weekends, anytime) | P1 |
| US-18 | As a musician, I can edit my profile at any time | P0 |
| US-19 | As a musician, my location is captured (with permission) for proximity matching | P0 |

### Discovery & Swiping
| ID | Story | Priority |
|----|-------|----------|
| US-20 | As a musician, I see a deck of nearby musicians I haven't swiped on yet | P0 |
| US-21 | As a musician, I can swipe right (interested) or left (pass) on a profile | P0 |
| US-22 | As a musician, I can set a max distance filter (5–100 miles) | P0 |
| US-23 | As a musician, I can filter by genre and/or instrument | P1 |
| US-24 | As a musician, I can filter by skill level | P2 |
| US-25 | As a musician, profiles are ranked by genre/instrument overlap score | P1 |
| US-26 | As a musician, I cannot swipe on the same person twice | P0 |

### Matching
| ID | Story | Priority |
|----|-------|----------|
| US-30 | As a musician, when I swipe right and the other person already swiped right on me, we match | P0 |
| US-31 | As a musician, I get a notification when a new match happens | P0 |
| US-32 | As a musician, I can see a list of all my matches | P0 |
| US-33 | As a musician, I can unmatch someone | P1 |

### Chat
| ID | Story | Priority |
|----|-------|----------|
| US-40 | As a matched musician, I can send text messages in real time | P0 |
| US-41 | As a matched musician, I can share audio clips in chat | P2 |
| US-42 | As a musician, I see unread message badges | P1 |
| US-43 | As a musician, I receive push notifications for new messages | P1 |

### Bands & Jam Sessions
| ID | Story | Priority |
|----|-------|----------|
| US-50 | As a musician, I can create a "Band" from one or more matches | P1 |
| US-51 | As a band creator, I can name the band and set a genre/vibe | P1 |
| US-52 | As a band member, I can see a group chat for the band | P1 |
| US-53 | As a musician, I can create a "Jam Session" event with date/time/location | P1 |
| US-54 | As a musician, I can invite matches to a jam session | P1 |
| US-55 | As a musician, I can see upcoming jam sessions on a simple calendar | P2 |

---

## 2. Database Schema

```
┌─────────────────────────────────────────────────────────────────┐
│                        DATABASE SCHEMA                          │
│                    PostgreSQL + PostGIS                          │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────┐       ┌──────────────────────────┐
│        users             │       │    musician_profiles     │
├──────────────────────────┤       ├──────────────────────────┤
│ id           UUID   PK   │──1:1──│ id           UUID   PK   │
│ email        VARCHAR UQ  │       │ user_id      UUID   FK   │
│ password_hash VARCHAR    │       │ display_name VARCHAR     │
│ auth_provider VARCHAR    │       │ bio          TEXT(500)   │
│ firebase_uid  VARCHAR    │       │ skill_level  ENUM       │
│ is_active    BOOLEAN     │       │ availability ENUM[]     │
│ created_at   TIMESTAMP   │       │ photo_url    VARCHAR     │
│ updated_at   TIMESTAMP   │       │ audio_intro_url VARCHAR  │
└──────────────────────────┘       │ location     GEOGRAPHY   │
                                   │   (Point, 4326)          │
                                   │ latitude     DOUBLE      │
                                   │ longitude    DOUBLE      │
                                   │ max_distance INT (miles) │
                                   │ soundcloud   VARCHAR     │
                                   │ spotify      VARCHAR     │
                                   │ youtube      VARCHAR     │
                                   │ bandcamp     VARCHAR     │
                                   │ created_at   TIMESTAMP   │
                                   │ updated_at   TIMESTAMP   │
                                   └──────────────────────────┘
                                          │           │
                                         M:N         M:N
                                          │           │
                           ┌──────────────┘           └──────────────┐
                           ▼                                         ▼
                  ┌──────────────────┐                  ┌──────────────────┐
                  │  instruments     │                  │     genres       │
                  ├──────────────────┤                  ├──────────────────┤
                  │ id      INT  PK  │                  │ id      INT  PK  │
                  │ name    VARCHAR  │                  │ name    VARCHAR  │
                  │ icon    VARCHAR  │                  │ icon    VARCHAR  │
                  └──────────────────┘                  └──────────────────┘
                           ▲                                         ▲
               ┌───────────┴───────────┐                ┌────────────┴──────────┐
               │ profile_instruments   │                │   profile_genres      │
               ├───────────────────────┤                ├───────────────────────┤
               │ profile_id  UUID FK   │                │ profile_id  UUID FK   │
               │ instrument_id INT FK  │                │ genre_id    INT  FK   │
               └───────────────────────┘                └───────────────────────┘

┌──────────────────────────┐
│     swipe_history        │
├──────────────────────────┤
│ id           UUID   PK   │
│ swiper_id    UUID   FK   │  → users.id
│ swiped_id    UUID   FK   │  → users.id
│ direction    ENUM        │  (LEFT, RIGHT)
│ created_at   TIMESTAMP   │
│ UNIQUE(swiper_id,        │
│        swiped_id)        │
└──────────────────────────┘

┌──────────────────────────┐
│        matches           │
├──────────────────────────┤
│ id           UUID   PK   │
│ user1_id     UUID   FK   │  → users.id (lower UUID)
│ user2_id     UUID   FK   │  → users.id (higher UUID)
│ matched_at   TIMESTAMP   │
│ is_active    BOOLEAN     │
│ UNIQUE(user1_id,         │
│        user2_id)         │
└──────────────────────────┘

┌──────────────────────────┐        ┌──────────────────────────┐
│        bands             │        │     band_members         │
├──────────────────────────┤        ├──────────────────────────┤
│ id           UUID   PK   │──1:N──│ id           UUID   PK   │
│ name         VARCHAR     │        │ band_id      UUID   FK   │
│ genre        VARCHAR     │        │ user_id      UUID   FK   │
│ bio          TEXT         │        │ role         VARCHAR     │
│ photo_url    VARCHAR     │        │ joined_at    TIMESTAMP   │
│ created_by   UUID   FK   │        └──────────────────────────┘
│ created_at   TIMESTAMP   │
└──────────────────────────┘

┌──────────────────────────┐        ┌──────────────────────────┐
│      jam_events          │        │   jam_event_attendees    │
├──────────────────────────┤        ├──────────────────────────┤
│ id           UUID   PK   │──1:N──│ id           UUID   PK   │
│ title        VARCHAR     │        │ event_id     UUID   FK   │
│ description  TEXT         │        │ user_id      UUID   FK   │
│ location_name VARCHAR    │        │ status       ENUM        │
│ location     GEOGRAPHY   │        │   (GOING,MAYBE,DECLINED) │
│ event_date   TIMESTAMP   │        │ responded_at TIMESTAMP   │
│ created_by   UUID   FK   │        └──────────────────────────┘
│ band_id      UUID FK NULL│
│ created_at   TIMESTAMP   │
└──────────────────────────┘

┌──────────────────────────┐
│    chat_messages         │
├──────────────────────────┤
│  (Stored in Firebase     │
│   Realtime DB / Firestore│
│   for real-time sync)    │
│                          │
│ id           STRING      │
│ match_id     STRING      │  (or band_id for group chat)
│ sender_id    STRING      │
│ text         STRING      │
│ audio_url    STRING?     │
│ timestamp    TIMESTAMP   │
│ read_by      STRING[]    │
└──────────────────────────┘
```

### Enum Definitions
```
skill_level:   BEGINNER | INTERMEDIATE | PRO
availability:  WEEKDAY_MORNINGS | WEEKDAY_EVENINGS | WEEKENDS | ANYTIME
swipe_direction: LEFT | RIGHT
rsvp_status:   GOING | MAYBE | DECLINED
auth_provider: EMAIL | GOOGLE | APPLE
```

---

## 3. API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register with email + password |
| POST | `/api/v1/auth/login` | Login, returns JWT |
| POST | `/api/v1/auth/google` | Google OAuth sign-in |
| POST | `/api/v1/auth/refresh` | Refresh JWT token |
| POST | `/api/v1/auth/forgot-password` | Send password reset email |

### Musician Profile
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/profile` | Create musician profile |
| GET | `/api/v1/profile/me` | Get current user's profile |
| PUT | `/api/v1/profile/me` | Update profile |
| POST | `/api/v1/profile/me/photo` | Upload profile photo (→ Cloudinary) |
| POST | `/api/v1/profile/me/audio` | Upload 15s audio intro (→ Cloudinary) |
| PUT | `/api/v1/profile/me/location` | Update lat/long |
| GET | `/api/v1/profile/{userId}` | View another user's profile |

### Reference Data
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/instruments` | List all instruments |
| GET | `/api/v1/genres` | List all genres |

### Discovery & Swiping
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/discovery/feed` | Get ranked nearby profiles (paginated) |
| | | Query params: `maxDistance`, `genres`, `instruments`, `skillLevel` |
| POST | `/api/v1/swipe` | Record a swipe `{ targetUserId, direction }` |
| | | Returns `{ matched: true/false }` |

### Matches
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/matches` | List all active matches |
| DELETE | `/api/v1/matches/{matchId}` | Unmatch |

### Bands
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/bands` | Create a band |
| GET | `/api/v1/bands` | List user's bands |
| GET | `/api/v1/bands/{bandId}` | Band details + members |
| POST | `/api/v1/bands/{bandId}/members` | Invite/add a member |
| DELETE | `/api/v1/bands/{bandId}/members/{userId}` | Remove member |

### Jam Sessions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/jams` | Create a jam session event |
| GET | `/api/v1/jams` | List upcoming jam sessions |
| GET | `/api/v1/jams/{jamId}` | Jam session details |
| POST | `/api/v1/jams/{jamId}/rsvp` | RSVP to a jam `{ status }` |

### Notifications (Firebase-driven)
- Push notifications sent server-side via Firebase Admin SDK
- No REST endpoint needed — client registers FCM token:

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/api/v1/notifications/token` | Register/update FCM device token |

### Real-Time (Firebase Firestore)
- Chat messages: `/chats/{matchId}/messages/{messageId}`
- Band group chat: `/chats/band_{bandId}/messages/{messageId}`
- Typing indicators: `/chats/{chatId}/typing/{userId}`
- No WebSocket on Spring Boot side — Firebase handles real-time sync to clients directly.

---

## 4. Screens & User Flow

### Flow Diagram
```
┌─────────┐    ┌──────────┐    ┌─────────────────┐    ┌────────────┐
│ Welcome  │───▶│  Sign Up │───▶│ Profile Wizard  │───▶│ Swipe Deck │
│ Screen   │    │  / Login │    │ (4 steps)       │    │  (Home)    │
└─────────┘    └──────────┘    └─────────────────┘    └─────┬──────┘
                                                            │
                                    ┌───────────────────────┼───────────────┐
                                    │                       │               │
                               ┌────▼─────┐         ┌──────▼───┐    ┌──────▼──────┐
                               │ Matches  │         │ Profile  │    │   Bands     │
                               │ List     │         │ (Edit)   │    │   & Jams    │
                               └────┬─────┘         └──────────┘    └──────┬──────┘
                                    │                                      │
                               ┌────▼─────┐                         ┌──────▼──────┐
                               │  Chat    │                         │ Band Detail │
                               │  Screen  │                         │ / Jam Detail│
                               └──────────┘                         └─────────────┘
```

### Screen Descriptions

#### 1. Welcome Screen
- App logo + tagline: "Find your sound. Find your band."
- "Sign Up" and "Log In" buttons
- Google sign-in button

#### 2. Sign Up / Log In
- Email + password form
- Google OAuth button
- Link to forgot password

#### 3. Profile Creation Wizard (4 steps)
**Step 1 – Basics:** Display name, profile photo upload, bio textarea
**Step 2 – Music:** Instrument chips (tap to select multiple), genre tag cloud, skill level slider (Beginner → Intermediate → Pro)
**Step 3 – Media:** Optional 15s audio intro (record or upload), links to SoundCloud/Spotify/YouTube/Bandcamp
**Step 4 – Preferences:** Availability checkboxes, max distance slider (5–100 mi), location permission prompt

#### 4. Swipe Deck (Home Tab)
- Card stack showing one profile at a time
- Each card shows: photo, name, instruments (icon chips), genres (tags), distance ("2.3 mi away"), skill level badge
- Tap card to expand → full profile with bio, audio player, music links
- Swipe right = interested, swipe left = pass
- Buttons below card: ✕ (pass) and ♫ (interested)
- "It's a match!" modal when mutual swipe detected

#### 5. Matches Tab
- List of matched musicians with avatar, name, last message preview
- Unread badge
- Tap → opens Chat Screen

#### 6. Chat Screen
- Standard messaging UI (bubbles, timestamps)
- Audio clip sharing button
- "Create Band" and "Propose Jam" action buttons in header

#### 7. Profile Tab (My Profile)
- View/edit all profile fields
- Preview "how others see me" card
- Settings (notifications, distance, logout)

#### 8. Bands & Jams Tab
- **My Bands** section: list of bands with name, members, genre
- **Upcoming Jams** section: calendar-style list of events
- Tap band → Band Detail (group chat + member list)
- Tap jam → Jam Detail (RSVP, location map, attendees)

### Bottom Tab Navigation
```
[ 🎵 Discover ]  [ 💬 Matches ]  [ 🎸 Bands ]  [ 👤 Profile ]
```

---

## 5. Project Structure (Preview)

```
bandswipe/
├── backend/                          # Spring Boot
│   ├── src/main/java/com/bandswipe/
│   │   ├── config/                   # Security, CORS, Firebase
│   │   ├── auth/                     # Auth controller, service, DTOs
│   │   ├── profile/                  # Profile controller, service, entity
│   │   ├── discovery/                # Feed algorithm, geo queries
│   │   ├── swipe/                    # Swipe controller, service, entity
│   │   ├── match/                    # Match controller, service, entity
│   │   ├── band/                     # Band controller, service, entity
│   │   ├── jam/                      # Jam event controller, service, entity
│   │   ├── notification/             # Firebase push notifications
│   │   └── shared/                   # Base entity, enums, exceptions
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── db/migration/             # Flyway migrations
│   │   └── firebase-service-account.json
│   ├── docker-compose.yml            # PostgreSQL + PostGIS
│   ├── build.gradle
│   └── Dockerfile
│
├── mobile/                           # React Native (Expo)
│   ├── app/                          # Expo Router screens
│   │   ├── (auth)/                   # Login, signup
│   │   ├── (onboarding)/             # Profile wizard steps
│   │   ├── (tabs)/                   # Main tab navigation
│   │   │   ├── discover.tsx          # Swipe deck
│   │   │   ├── matches.tsx           # Match list
│   │   │   ├── bands.tsx             # Bands & jams
│   │   │   └── profile.tsx           # My profile
│   │   ├── chat/[matchId].tsx        # Chat screen
│   │   ├── band/[bandId].tsx         # Band detail
│   │   └── jam/[jamId].tsx           # Jam detail
│   ├── components/                   # Shared components
│   ├── services/                     # API client, Firebase
│   ├── stores/                       # Zustand state
│   ├── hooks/                        # Custom hooks
│   ├── constants/                    # Theme, config
│   ├── app.json
│   ├── tsconfig.json
│   └── package.json
│
└── STAGE0_PLANNING.md
```

---

## 6. Tech Decisions Summary

| Concern | Choice | Rationale |
|---------|--------|-----------|
| Auth | Firebase Auth + JWT | Firebase handles OAuth providers; Spring Boot verifies Firebase ID tokens and issues its own JWTs for API access |
| Real-time chat | Firestore | Built-in real-time sync, offline support, no need for custom WebSocket server |
| Geo queries | PostGIS `ST_DWithin` | Industry-standard spatial queries, index-friendly |
| Image/audio storage | Cloudinary | CDN, transformations, 15s audio clip support |
| State management | Zustand | Lightweight, TypeScript-friendly |
| Navigation | Expo Router v3 | File-based routing, native feel |
| Swipe cards | react-native-deck-swiper or custom gesture | Tinder-style UX |
| DB migrations | Flyway | Version-controlled SQL migrations |

---

*Ready for Stage 1?*
