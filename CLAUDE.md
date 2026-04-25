# EduGroup Optimizer — Project Instructions

## Role
Senior Fullstack Developer & Architect.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.x, Maven, Spring Data JPA, PostgreSQL (prod) / H2 (dev)
- **Frontend:** React 18+ (Vite), Tailwind CSS, shadcn/ui, Lucide Icons, react-i18next
- **Architecture:** Monorepo with `backend/` and `frontend/` directories

## Code Standards

### General
- **No comments in code.** Exception: complex algorithms that truly need clarification.
- **All naming in English:** variables, classes, methods, DB tables, endpoints.
- **camelCase** for JavaScript/TypeScript and Java variables/methods.
- **PascalCase** for Java classes and React components.
- **snake_case** for database columns and table names.
- Follow **Clean Code** principles rigorously.
- Follow **SOLID** principles in all backend services.

### Backend (Java / Spring Boot)
- Use **DTOs** for all API request/response payloads. Never expose entities directly.
- Use **records** for DTOs when possible (Java 21).
- Use **constructor injection** (no @Autowired on fields).
- Package structure: `com.edugroup.optimizer.{module}.{layer}`
  - Layers: `controller`, `service`, `repository`, `entity`, `dto`, `config`, `exception`
- Validate inputs with `@Valid` and Jakarta Validation annotations.
- Use `ResponseEntity<>` for all controller responses.
- Global exception handling via `@RestControllerAdvice`.
- REST endpoints follow: `GET /api/v1/{resource}`, `POST /api/v1/{resource}`, etc.

### Frontend (React / Vite)
- Functional components only. Use hooks (`useState`, `useEffect`, `useContext`, `useReducer`).
- File structure: `src/components/`, `src/pages/`, `src/services/`, `src/hooks/`, `src/i18n/`, `src/types/`
- API calls in `src/services/` using `fetch` or `axios`.
- All UI text must go through `react-i18next`. Default language: Spanish (`es`).
- Use **shadcn/ui** for accessible component primitives.
- Use **Tailwind CSS** utility-first. No custom CSS files unless strictly necessary.
- Responsive design: mobile-first. Must work on tablets and phones.

## Business Rules
- Compatibility scale: **-5 to +5** (integer).
- **Hard constraint:** Students with score **≤ −4** must NEVER be in the same group.
- Grouping algorithm must maximize:
  1. Rotation history (avoid repeating past groupings).
  2. Overall group compatibility (sum of pairwise scores).
- Each grouping session is saved with timestamp and metadata.

## Git Workflow
- Main branch: `main`
- Development branch: `dev` (created from `main`)
- All commits go to `dev` first.
- **One commit per functional unit.** Push one at a time.
- Commit messages in English, imperative mood: `Add student entity`, `Create grouping algorithm service`

## API Design
- Base path: `/api/v1`
- Content-Type: `application/json`
- Use proper HTTP status codes (200, 201, 204, 400, 404, 409, 422, 500).
- Pagination for list endpoints using `page` and `size` query params.
