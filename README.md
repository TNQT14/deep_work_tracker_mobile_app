# Deep Work Tracker

A mobile application for tracking and analyzing personal focus capabilities, built with Clean Architecture, MVVM, and Jetpack Compose.

## Architecture

The project follows **Clean Architecture** principles with the following structure:

```
app/                    # Main application module
├── feature:session/    # Focus Session feature
├── feature:dashboard/  # Dashboard feature
├── core:domain/       # Domain layer (pure Kotlin)
├── core:data/         # Data layer (Room, Repositories)
├── core:common/       # Common utilities
└── core:ui/           # Shared UI components
```

## Tech Stack

- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt (Dagger Hilt)
- **Database**: Room
- **Async**: Coroutines + Flow
- **Navigation**: Navigation Compose

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run the app

## Project Structure

### Core Modules

- **core:domain**: Pure Kotlin domain models and repository interfaces
- **core:data**: Room database, entities, DAOs, and repository implementations
- **core:common**: Common utilities (Result, TimeFormatter, etc.)
- **core:ui**: Shared UI components and theme

### Feature Modules

- **feature:session**: Focus session management (start, pause, resume, end)
- **feature:dashboard**: Statistics and insights display

## Features (MVP)

- ✅ Start/End focus sessions
- ✅ Track session time
- ✅ Basic dashboard
- 🔄 Interruption tracking (in progress)
- 🔄 Statistics calculation (in progress)
- 🔄 Insights generation (in progress)

## Development

### Building

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Running the App

```bash
./gradlew installDebug
```

## Next Steps

See `IMPLEMENTATION_ROADMAP.md` for detailed development plan.

## Documentation

- `ARCHITECTURE_ANALYSIS.md` - Architecture analysis and design decisions
- `TECHNICAL_SPECIFICATION.md` - Technical specifications
- `IMPLEMENTATION_ROADMAP.md` - Development roadmap
- `ARCHITECTURE_AND_BASE_LIBS.md` - Architecture and base libraries guide
