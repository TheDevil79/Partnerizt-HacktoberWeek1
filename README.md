# Partnerizt 🌱🧭
> **"The Duolingo for learning from your environment."**

Partnerizt is a gamified outdoor learning platform that encourages users to step outside, explore their physical surroundings, complete real-world quests, photograph discoveries, and learn from AI learning companions.

---

## 🏗️ Monorepo Architecture

```
partnerizt/
├── frontend/                     # React + TypeScript + Vite + Tailwind CSS
│   ├── public/                   # Static assets & SVG icons
│   ├── src/
│   │   ├── assets/               # Bespoke SVG Character Avatars (Birdo, Flora, Atlas, Munch, Nova)
│   │   ├── components/
│   │   │   ├── common/           # Header, XPBar, StatPill, BadgeCard, ConfettiCelebration, Modal
│   │   │   ├── home/             # ExplorationHero, DailyProgressCard, DailyQuestCard, RecentDiscoveryCard
│   │   │   ├── quests/           # QuestCard, QuestFilterTabs, QuestDetailsModal, QuestPhotoUploadModal
│   │   │   ├── characters/       # CharacterCard, CharacterChatModal
│   │   │   └── profile/          # ProfileHeader, StatsGrid, BadgesShowcase, DiscoveryGallery
│   │   ├── context/              # PartneriztContext (Global State, Geolocation Session, Rewards)
│   │   ├── layouts/              # MainLayout, Mobile BottomNav & DesktopNav
│   │   ├── pages/                # HomePage, QuestsPage, CharactersPage, ProfilePage
│   │   ├── services/             # API client, Exploration Haversine Tracker, Web Audio Synthesizer, Mock Data
│   │   └── types/                # TypeScript Interfaces for Quests, Characters, Discoveries, User
│   └── package.json
└── backend/                      # Java + Spring Boot + Maven + PostgreSQL (Prepared for Phase 2)
    ├── src/main/java/com/partnerizt/
    ├── src/main/resources/application.yml
    └── pom.xml
```

---

## 🌟 Core Features Implemented in Phase 1

1. **Mobile-First Bottom Navigation & Responsive Shell**:
   - 4 Core sections: **Home**, **Quests**, **Characters**, and **Profile**.
   - Tactile Duolingo-inspired 3D buttons, bouncy rewards, and nature palette.

2. **Exploration Engine (Distance-based)**:
   - **`[ START EXPLORING ]`** session with live GPS coordinate tracking via browser Geolocation API (`navigator.geolocation`).
   - Haversine formula calculation in kilometers (not fake step counters).
   - Live HUD with distance, active timer duration, session XP bonuses, and walk simulation mode.

3. **Duolingo-Inspired Quest System**:
   - Seeded challenges across Wildlife, Plants, Architecture, Food, and Geology.
   - Timer countdowns, target hints, XP & coin awards.
   - Interactive Photo Upload & simulated Gemma + SerpApi verification pipeline.

4. **5 AI Learning Companions**:
   - 🐦 **Birdo**: Wildlife, birds, animals (Energetic, curious).
   - 🌿 **Flora**: Botany, trees, flowers (Calm, wise).
   - 🏛️ **Atlas**: Architecture, history, monuments (Adventurous storyteller).
   - 🫐 **Munch**: Foraging, edible plants, food culture (Enthusiastic foodie).
   - ✨ **Nova**: Geology, science, astronomy (Smart, curious).
   - Domain-tailored chat interface with quick suggestion chips, actionable outdoor activities, and verified knowledge citations.

5. **Gamified Profile & Badges**:
   - Level progression bar with XP formula.
   - Streak counters, coin wallets, all-time distance and time metrics.
   - 7 Initial Badges: *First Exploration, First Discovery, Wildlife Explorer, Nature Explorer, History Hunter, 7 Day Explorer, TouchGrass Master*.
   - Discovery photo catalog.

---

## 🚀 Running the Full-Stack Application

### 1. Spring Boot Backend (Java 17 + Maven)

```bash
cd backend
mvn spring-boot:run
```
The REST API will start on `http://localhost:8080`.

#### Backend API Endpoints:
- `GET /api/v1/users/me` — Current user profile and progress
- `GET /api/v1/users/stats` — Detailed user metrics, distance, and badge unlock status
- `GET /api/v1/companions` — List all domain-expert learning companions (Oakley, Solara, Geode, Archimedes, Nova)
- `GET /api/v1/companions/{id}` — Companion profile, personality, and outdoor activity suggestions
- `GET /api/v1/quests` — All outdoor quests
- `GET /api/v1/quests/daily` — Daily outdoor missions with countdown timers
- `POST /api/v1/quests/{id}/start` — Start a quest
- `POST /api/v1/quests/{id}/complete` — Complete a quest with field photo evidence & award XP/coins
- `POST /api/v1/exploration/sessions/start` — Start GPS-based physical outdoor exploration session
- `GET /api/v1/exploration/sessions/active` — Active exploration session HUD metrics
- `PUT /api/v1/exploration/sessions/{id}/update` — Update real-time GPS distance and duration
- `POST /api/v1/exploration/sessions/{id}/complete` — End exploration and calculate earned XP/coins
- `GET /api/v1/discoveries` — User's verified Field Journal catalog
- `POST /api/v1/discoveries` — Log a new outdoor specimen discovery
- `POST /api/v1/discoveries/identify` — AI-powered visual specimen identification and educational commentary
- `GET /api/v1/chat/{companionId}/messages` — Companion chat conversation history
- `POST /api/v1/chat/{companionId}/send` — Ask companion about outdoor observations

### 2. React + TypeScript Frontend

```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.
