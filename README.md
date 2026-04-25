# EduGroup Optimizer

Intelligent classroom grouping tool that maximizes student compatibility and minimizes repeated pairings across sessions.

## Overview

EduGroup Optimizer helps educators create balanced student groups by tracking compatibility scores between students and rotation history. The algorithm ensures students with strong incompatibility (score ≤ −4) are never placed together, while maximizing overall group harmony and variety.

## Tech Stack

- **Backend:** Java 21, Spring Boot 3.x, PostgreSQL (prod) / H2 (dev)
- **Frontend:** React 18+, Vite, TypeScript, Tailwind CSS, shadcn/ui

## Project Structure

```
edugroup-optimizer/
├── backend/          # Spring Boot API
└── frontend/         # React + Vite SPA
```

## Getting Started

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

API runs on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

App runs on `http://localhost:5173`.

## Business Rules

- Compatibility scores range from **−5 to +5**.
- Students with a score **≤ −4** are never placed in the same group.
- Grouping algorithm prioritizes rotation history, then overall compatibility.
