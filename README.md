# Deep Work Tracker

> A cognitive behavior measurement platform for analyzing personal focus capabilities through quantitative data tracking and pattern analysis.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.09-green.svg)](https://developer.android.com/jetpack/compose)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange.svg)]()

---

## 📖 Overview

**Deep Work Tracker** is an Android application that goes beyond simple time tracking to provide deep insights into cognitive work patterns. Unlike traditional productivity apps that merely count minutes, this platform acts as a "microscope for focus" — measuring real concentration time, identifying disruption patterns, optimal working hours, and flow-breaking triggers.

### Problem Statement

Most productivity applications fail to answer critical questions:
- What is my **actual** focus time (excluding distractions)?
- When do my concentration breaks occur?
- Which time windows yield my best work?
- What causes my flow state to break?

This app addresses these gaps through behavioral data collection, interruption tracking, and pattern analysis.

### Target Users

- **Software Engineers** tracking deep coding sessions
- **Technical Students** optimizing study efficiency
- **Knowledge Workers** performing high-intensity cognitive work
- **Researchers & Learners** monitoring learning progress
- **Quantified Self Enthusiasts** seeking self-optimization data

---

## ✨ Key Features

### Current Implementation
- ✅ **Focus Session Management**: Start, pause, resume, and end focus sessions with goal tracking
- ✅ **Dashboard Analytics**: Today's stats with session count, total focus time, and average duration
- ✅ **Goal Distribution Visualization**: Pie chart breakdown of time spent across different goals
- ✅ **Goal-Specific Tracking**: Tap any goal to view detailed session history (last 30 days)
- ✅ **Recent Sessions Timeline**: Chronological view of completed focus sessions
- ✅ **Time-Based Insights**: Best focus hour detection based on historical patterns

### Planned Features
- 🔄 **Interruption Tracking**: Automatic detection of background switches, screen locks, and app exits
- 🔄 **Advanced Statistics**: Weekly/monthly aggregations with trend analysis
- 🔄 **Insight Engine**: Rule-based pattern detection → ML-powered recommendations
- 🔄 **Smart Notifications**: Context-aware reminders based on historical patterns
- 🔄 **Export & Backup**: Data portability and privacy controls

---

## 🏗️ Architecture & Design

### Architectural Pattern: Clean Architecture + MVVM

The project implements **Clean Architecture** with strict layer separation and **MVVM** for presentation orchestration. The architecture enforces the **Dependency Rule**: source code dependencies point inward, with inner layers knowing nothing about outer layers.

```
┌───────────────────────────────────────────────────────────────────────┐
│                         Presentation Layer                            │
│  ┌─────────────┐    ┌──────────────┐    ┌─────────────────────┐     │
│  │   Compose   │───▶│  ViewModel   │───▶│  StateFlow<UiState> │     │
│  │   Screens   │◀───│  (UI Logic)  │◀───│  (Unidirectional)   │     │
│  └─────────────┘    └──────┬───────┘    └─────────────────────┘     │
│                             │ observes Flow<DomainModel>             │
└─────────────────────────────┼────────────────────────────────────────┘
                              │ depends on ↓
┌─────────────────────────────┼────────────────────────────────────────┐
│                      Domain Layer (Pure Kotlin)                      │
│                             ▼                                         │
│  ┌──────────────┐    ┌─────────────────┐    ┌──────────────────┐   │
│  │  Use Cases   │───▶│   Repository    │    │  Domain Models   │   │
│  │ (Operators)  │    │   Interfaces    │    │  (Entities)      │   │
│  └──────────────┘    └─────────────────┘    └──────────────────┘   │
│                              ▲ contracts only                        │
└──────────────────────────────┼───────────────────────────────────────┘
                               │ implemented by ↓
┌──────────────────────────────┼───────────────────────────────────────┐
│                         Data Layer                                   │
│                              ▼                                        │
│  ┌────────────────┐   ┌─────────────────┐   ┌──────────────────┐   │
│  │  Repository    │──▶│   Room DAO      │──▶│  Room Entities   │   │
│  │  Impl          │   │   (SQL)         │   │  (@Entity)       │   │
│  └────────────────┘   └─────────────────┘   └──────────────────┘   │
│         │                      │                                     │
│         ▼                      ▼                                     │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │              SQLite Database (Single Source of Truth)       │    │
│  └────────────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────────────┘
```

### Architectural Assumptions & Constraints

**Explicit Assumptions:**
1. **Domain Purity**: The `:core:domain` module contains zero Android dependencies → testable in pure JVM tests
2. **Repository Abstraction**: Data sources are abstracted behind repository interfaces → swappable implementations (e.g., Room → remote API)
3. **Mapper Pattern**: Entity-to-Domain mapping occurs exclusively in the data layer → domain models remain persistence-agnostic
4. **Single Database Instance**: Room database is a singleton managed by Hilt → no multi-instance concurrency issues
5. **Flow-Based Reactivity**: Database queries return `Flow<T>` → UI updates automatically on data changes without manual refresh
6. **No Network Layer Yet**: All data operations are local → future API integration will slot into existing repository contracts

**Assumed Constraints:**
- **No Shared ViewModels**: Each screen has its own ViewModel → no cross-feature state coupling
- **No Fragment Usage**: Pure Compose navigation → simpler lifecycle management
- **Foreground-Only Session Tracking**: (Likely) No background service yet → app must stay open during sessions
- **No Multi-User Support**: Database schema assumes single-user device → no user_id foreign keys

### Module Structure

The project is organized into **multi-module architecture** for scalability and compile-time optimization:

#### Core Modules (`:core`)

| Module | Purpose | Key Components |
|--------|---------|----------------|
| `:core:domain` | Pure Kotlin business logic | Domain models (`FocusSession`, `Interruption`, `Insight`, `DailyStats`), Repository interfaces |
| `:core:data` | Data persistence & sources | Room database (`SessionDao`, `InterruptionDao`), Repository implementations, Entity mappers |
| `:core:common` | Shared utilities | `Result` wrapper, `TimeFormatter`, `DeepWorkError` hierarchy |
| `:core:ui` | UI foundations | Material 3 theme, `Color`, `Type`, shared composables |

#### Feature Modules (`:feature`)

| Module | Purpose | Screens | ViewModels |
|--------|---------|---------|------------|
| `:feature:session` | Focus session lifecycle | `SessionScreen` | `SessionViewModel` |
| `:feature:dashboard` | Analytics & insights | `DashboardScreen`, `GoalDetailScreen` | `DashboardViewModel`, `GoalDetailViewModel` |

### State Management Pattern

**Unidirectional Data Flow (UDF) Implementation:**

```kotlin
// Pattern observed in ViewModels:
sealed class UiState {
    data class Success(val data: DomainModel) : UiState()
    data class Error(val exception: DeepWorkError) : UiState()
    object Loading : UiState()
}

class FeatureViewModel @Inject constructor(
    private val useCase: UseCase
) : ViewModel() {
    
    // 1. Internal mutable state (private)
    private val _uiState = MutableStateFlow<UiState>(Loading)
    
    // 2. External immutable state (public)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // 3. Events from UI trigger state updates
    fun onUserAction() {
        viewModelScope.launch {
            useCase().collect { result ->
                _uiState.value = when(result) {
                    is Result.Success -> UiState.Success(result.data)
                    is Result.Error -> UiState.Error(result.exception)
                }
            }
        }
    }
}
```

**State Flow Characteristics:**
- **Hot Stream**: StateFlow is always active, caches latest value
- **Conflation**: UI only receives latest state (intermediate states may be skipped)
- **Lifecycle-Aware**: Compose collects safely via `collectAsState()` with automatic cancellation
- **Thread-Safe**: State updates serialized via coroutine dispatcher

### Dependency Injection Architecture

**Hilt Module Structure (Inferred):**

```
@HiltAndroidApp
MainActivity
    │
    ├─► @AndroidEntryPoint DashboardScreen
    │       └─► @HiltViewModel DashboardViewModel
    │               ├─► @Inject GetTodayStatsUseCase
    │               ├─► @Inject GetRecentSessionsUseCase
    │               └─► @Inject GetAllSessionUseCase
    │                       └─► @Inject SessionRepository (interface)
    │
    └─► @AndroidEntryPoint SessionScreen
            └─► @HiltViewModel SessionViewModel
                    └─► @Inject StartSessionUseCase
                            └─► @Inject SessionRepository (interface)

@Module @InstallIn(SingletonComponent::class)
DataModule {
    @Provides @Singleton
    fun provideDatabase(): DeepWorkDatabase
    
    @Provides @Singleton
    fun provideSessionDao(db): SessionDao
    
    @Binds @Singleton
    SessionRepository ← SessionRepositoryImpl
}
```

**Scoping Strategy:**
- **@Singleton**: Database, DAOs, Repositories (app-level)
- **@ViewModelScoped**: Use Cases (ViewModel-level, likely)
- **@ActivityRetainedScoped**: Potentially for navigation state
- **No @ActivityScoped**: Pure Compose → no Activity-scoped components

### Key Design Decisions

1. **Unidirectional Data Flow (UDF)**: ViewModels expose `StateFlow<UiState>`, UI observes and renders declaratively → eliminates view update bugs
2. **Single Source of Truth (SSOT)**: Room database as authoritative → no state synchronization issues between caches
3. **Dependency Rule Enforcement**: Dependencies point inward (Data → Domain ← Presentation) → domain layer testable without Android framework
4. **Explicit Error Modeling**: Sealed `Result<T>` type wrapping success/error → compile-time exhaustive error handling
5. **Offline-First Design**: All features work without network connectivity → fast, reliable UX even on poor connections
6. **Type Safety via Domain Models**: No primitives leaking to UI (e.g., `Long` → `Duration`, `String` → `Goal` type) → prevents invalid states
7. **Reactive Streams**: `Flow` for multi-value emissions, `suspend` for single-shot operations → consistent async API
8. **Mapper Isolation**: Entity↔Domain mapping in data layer only → domain models remain persistence-agnostic for future backend swaps

---

## 🛠️ Tech Stack

### Android Core
- **Language**: Kotlin 2.0.21
- **Min SDK**: 33 (Android 13+)
- **Build System**: Gradle with Kotlin DSL + Version Catalog

### UI Layer
- **Framework**: Jetpack Compose (Material 3 Design)
- **Navigation**: Navigation Compose 2.8.4
- **Charts**: Vico 2.0.0-alpha (for bar charts and distribution visualizations)
- **Icons**: Material Icons Extended

### Architecture & DI
- **Pattern**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt (Dagger) 2.51.1
- **ViewModel**: Lifecycle ViewModel Compose

### Data & Persistence
- **Database**: Room 2.6.1 (SQLite ORM)
- **Preferences**: (TBD - likely DataStore)

### Asynchronous Programming
- **Coroutines**: Kotlin Coroutines 1.9.0
- **Reactive Streams**: Kotlin Flow
- **Lifecycle Awareness**: Lifecycle Runtime Compose

### Background Work
- **WorkManager**: 2.9.1 (for periodic stats aggregation, insight generation)
- **Foreground Service**: (Planned for active session tracking)

### Date/Time
- **Library**: kotlinx-datetime 0.6.0 (multiplatform time API)

### Logging
- **Timber**: 5.0.1

### Testing
- **Unit Testing**: JUnit 4, MockK 1.13.10
- **Flow Testing**: Turbine 1.1.0
- **Assertions**: Truth 1.4.2
- **Coroutine Testing**: kotlinx-coroutines-test

---

## 📂 Project Structure

```
deep_work_tracker_mobile_app/
│
├── app/                                    # Main application module
│   ├── src/main/java/com/deepworktracker/
│   │   └── MainActivity.kt                # Navigation host
│   └── build.gradle.kts
│
├── core/
│   ├── domain/                            # Pure Kotlin domain layer
│   │   └── src/main/java/com/.../domain/
│   │       ├── model/                     # Domain entities
│   │       │   ├── FocusSession.kt
│   │       │   ├── Interruption.kt
│   │       │   ├── Insight.kt
│   │       │   └── DailyStats.kt
│   │       ├── repository/                # Repository contracts
│   │       │   ├── SessionRepository.kt
│   │       │   ├── InterruptionRepository.kt
│   │       │   ├── StatsRepository.kt
│   │       │   └── InsightRepository.kt
│   │       └── error/
│   │           └── DeepWorkError.kt       # Domain error types
│   │
│   ├── data/                              # Data implementation layer
│   │   └── src/main/java/com/.../data/
│   │       ├── database/                  # Room components
│   │       │   ├── DeepWorkDatabase.kt
│   │       │   ├── SessionDao.kt
│   │       │   ├── InterruptionDao.kt
│   │       │   └── entities/              # Room entities
│   │       ├── repository/                # Repository implementations
│   │       │   ├── SessionRepositoryImpl.kt
│   │       │   └── ...
│   │       ├── mapper/                    # Entity ↔ Domain mappers
│   │       │   ├── SessionMapper.kt
│   │       │   └── ...
│   │       └── di/
│   │           └── DataModule.kt          # Hilt data bindings
│   │
│   ├── common/                            # Shared utilities
│   │   └── src/main/java/com/.../common/
│   │       ├── result/
│   │       │   └── Result.kt              # Result<T> wrapper
│   │       └── time/
│   │           └── TimeFormatter.kt       # Duration formatting
│   │
│   └── ui/                                # Shared UI components
│       └── src/main/java/com/.../ui/
│           └── theme/                     # Material 3 theming
│               ├── Color.kt
│               ├── Theme.kt
│               └── Type.kt
│
├── feature/
│   ├── session/                           # Session tracking feature
│   │   └── src/main/java/com/.../session/
│   │       ├── domain/usecase/
│   │       │   ├── StartSessionUseCase.kt
│   │       │   ├── EndSessionUseCase.kt
│   │       │   ├── GetActiveSessionUseCase.kt
│   │       │   └── GetRecentGoalsUseCase.kt
│   │       └── presentation/
│   │           ├── SessionScreen.kt
│   │           ├── SessionViewModel.kt
│   │           └── SessionUiState.kt
│   │
│   └── dashboard/                         # Analytics feature
│       └── src/main/java/com/.../dashboard/
│           ├── domain/usecase/
│           │   ├── GetTodayStatsUseCase.kt
│           │   ├── GetRecentSessionsUseCase.kt
│           │   └── GetAllSessionUseCase.kt  # NEW: Get all sessions
│           └── presentation/
│               ├── DashboardScreen.kt
│               ├── DashboardViewModel.kt
│               ├── DashboardUiState.kt
│               ├── GoalDetailScreen.kt      # NEW: Goal drill-down
│               ├── GoalDetailViewModel.kt   # NEW: Goal analytics
│               └── chart/
│                   ├── FocusTimeBarChart.kt
│                   └── GoalDistributionPieChart.kt
│
├── gradle/
│   └── libs.versions.toml                 # Centralized dependency management
│
├── ARCHITECTURE_ANALYSIS.md              # Detailed architecture documentation
├── TECHNICAL_SPECIFICATION.md            # Technical specs
├── IMPLEMENTATION_ROADMAP.md             # Development roadmap
└── README.md                             # This file
```

---

## 💾 Database Architecture

### Room Database Schema (Inferred)

Based on domain models and repository interfaces, the database likely contains:

```sql
-- SessionEntity (primary table)
CREATE TABLE sessions (
    id TEXT PRIMARY KEY NOT NULL,
    goal TEXT NOT NULL,
    start_time INTEGER NOT NULL,        -- Unix timestamp (millis)
    end_time INTEGER,                   -- Nullable for active sessions
    total_duration INTEGER NOT NULL,    -- Milliseconds
    is_active INTEGER NOT NULL,         -- Boolean (0/1)
    created_at INTEGER NOT NULL
);

-- InterruptionEntity (1:N relationship with Session)
CREATE TABLE interruptions (
    id TEXT PRIMARY KEY NOT NULL,
    session_id TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    type TEXT NOT NULL,                 -- "BACKGROUND", "SCREEN_LOCK", "APP_SWITCH"
    duration INTEGER,                   -- Milliseconds (nullable if not resumed)
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- InsightEntity (generated insights)
CREATE TABLE insights (
    id TEXT PRIMARY KEY NOT NULL,
    type TEXT NOT NULL,                 -- "PATTERN", "TREND", "RECOMMENDATION"
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    generated_at INTEGER NOT NULL,
    data TEXT                           -- JSON payload for charts/details
);

-- Indexes for query optimization (assumed)
CREATE INDEX idx_sessions_start_time ON sessions(start_time DESC);
CREATE INDEX idx_sessions_goal ON sessions(goal);
CREATE INDEX idx_interruptions_session ON interruptions(session_id);
```

**Schema Design Observations:**
- **Denormalization**: `total_duration` stored redundantly (likely calculated on insert/update) → optimizes read queries for dashboard
- **Temporal Queries**: Indexed `start_time` enables fast date range queries (`getSessionsByDateRange`)
- **Soft Boolean**: `is_active` stored as `INTEGER` (SQLite limitation) → Room TypeConverter likely used
- **Cascade Deletion**: Interruptions deleted when parent session deleted → maintains referential integrity
- **No User Table**: Single-user assumption → no authentication/multi-tenancy complexity

### Data Flow Patterns

**Pattern 1: Reactive Query (Dashboard)**
```kotlin
// Repository (Data Layer)
override fun getSessionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<FocusSession>> {
    return sessionDao.getSessionsByDateRange(start.toEpochDays(), end.toEpochDays())
        .map { entities -> entities.map { mapper.toDomain(it) } }
}

// Room DAO (emits new values on DB change)
@Query("SELECT * FROM sessions WHERE start_time >= :start AND start_time <= :end ORDER BY start_time DESC")
fun getSessionsByDateRange(start: Long, end: Long): Flow<List<SessionEntity>>

// ViewModel (Presentation Layer)
init {
    viewModelScope.launch {
        repository.getSessionsByDateRange(sevenDaysAgo, today).collect { sessions ->
            _uiState.update { it.copy(recentSessions = sessions, isLoading = false) }
        }
    }
}
```
**Result**: UI automatically updates when database changes (e.g., new session added) without manual refresh.

**Pattern 2: One-Shot Command (Start Session)**
```kotlin
// Use Case (Domain Layer)
suspend operator fun invoke(goal: String): Result<FocusSession> {
    val session = FocusSession(
        id = UUID.randomUUID().toString(),
        goal = goal,
        startTime = Clock.System.now(),
        isActive = true,
        totalDuration = 0L
    )
    return repository.saveSession(session).map { session }
}

// Repository (Data Layer) - wrapped in Result<T>
override suspend fun saveSession(session: FocusSession): Result<Unit> {
    return try {
        sessionDao.insertSession(mapper.toEntity(session))
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(DeepWorkError.DatabaseError(e.message))
    }
}
```
**Result**: Errors propagate up as typed domain errors, not exceptions.

**Pattern 3: Aggregation Query (Today's Stats)**
```kotlin
// Likely SQL query in DAO:
@Query("""
    SELECT 
        COUNT(*) as session_count,
        SUM(total_duration) as total_focus_time,
        AVG(total_duration) as avg_duration,
        strftime('%H', start_time / 1000, 'unixepoch', 'localtime') as hour
    FROM sessions 
    WHERE date(start_time / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')
    GROUP BY hour
    ORDER BY SUM(total_duration) DESC
    LIMIT 1
""")
suspend fun getTodayStats(): TodayStatsEntity?
```
**Result**: Database performs aggregation → lighter memory footprint than loading all sessions into memory.

**Pattern 4: One-Shot List Query (Get All Sessions)**
```kotlin
// Repository (Data Layer)
override suspend fun getAllSessions(): List<FocusSession> {
    return try {
        sessionDao.getAllSessions().first().map { mapper.toDomain(it) }
    } catch (e: Exception) {
        emptyList()
    }
}

// Room DAO (returns Flow, converted to List)
@Query("SELECT * FROM focus_sessions ORDER BY start_time DESC")
fun getAllSessions(): Flow<List<FocusSessionEntity>>

// Use Case (Domain Layer)
suspend operator fun invoke(): Result<List<FocusSession>> {
    return try {
        val allSessions = sessionRepository.getAllSessions()
        Result.Success(allSessions)
    } catch (e: Exception) {
        Result.Error(e)
    }
}

// ViewModel (Presentation Layer)
fun loadDashboardData() {
    viewModelScope.launch {
        when (val result = getAllSessionUseCase()) {
            is Result.Success -> {
                _uiState.update { it.copy(allSessions = result.data) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.exception) }
            }
        }
    }
}
```
**Result**: One-shot query for bulk data retrieval → useful for export, analytics, or filtering operations.

---

## 🔄 How the System Works

### High-Level Data Flow

```
User Interaction (Compose UI)
         ↓
ViewModel (handles events, business logic orchestration)
         ↓
Use Case (encapsulated business rule)
         ↓
Repository Interface (domain contract)
         ↓
Repository Implementation (data layer)
         ↓
Room DAO (database operations)
         ↓
SQLite Database (persistent storage)
         ↓
Flow<Data> streams back up the chain
         ↓
StateFlow<UiState> in ViewModel
         ↓
Compose UI recomposes reactively
```

### Example: Starting a Focus Session

1. User taps "Start Session" in `SessionScreen`
2. `SessionViewModel.startSession(goal)` is invoked
3. `StartSessionUseCase` creates a `FocusSession` domain object
4. `SessionRepositoryImpl.saveSession()` maps to `SessionEntity`
5. `SessionDao.insertSession()` writes to Room database
6. Database change triggers Flow emission
7. ViewModel updates `StateFlow<SessionUiState>`
8. Compose UI recomposes with new state

### Example: Viewing Goal Details (NEW)

1. User taps a goal in `GoalDistributionChart` on Dashboard
2. `DashboardScreen` invokes `onNavigateToGoal(goalName)`
3. `NavController` navigates to `"goal/{encodedGoalName}"` route
4. `GoalDetailScreen` is composed with decoded goal parameter
5. `GoalDetailViewModel.loadGoal(goal)` fetches sessions via repository
6. Sessions filtered by `goal` name (last 30 days)
7. Total duration calculated, sessions sorted by `startTime` descending
8. `StateFlow<GoalDetailUiState>` emits filtered data
9. UI displays total time + list of sessions (reuses `SessionCard`)

---

## 🧭 Navigation Architecture

### Navigation Graph Structure

```kotlin
// MainActivity.kt (single activity architecture)
NavHost(navController, startDestination = "session") {
    composable("session") { 
        SessionScreen(onNavigateToDashboard = { navController.navigate("dashboard") })
    }
    
    composable("dashboard") { 
        DashboardScreen(
            onNavigateToSession = { navController.navigate("session") },
            onNavigateToGoal = { goal -> navController.navigate("goal/${Uri.encode(goal)}") }
        )
    }
    
    composable("goal/{goal}") { backStackEntry ->
        val encodedGoal = backStackEntry.arguments?.getString("goal") ?: ""
        val goal = Uri.decode(encodedGoal)
        GoalDetailScreen(goal = goal)
    }
}
```

**Navigation Patterns:**
- **String-Based Routes**: Simple path-based navigation (assumed: no type-safe args yet)
- **Manual Encoding**: `Uri.encode/decode` for special characters in goal names → prevents route parsing errors
- **Callback-Based Navigation**: Parent screens pass callbacks to children → decouples screen from NavController
- **No Nested Navigation**: Flat graph structure → simpler back stack management

**Potential Issues & Improvements:**
- ❌ **Type Safety**: String routes prone to typos → migrate to Kotlin Serialization-based navigation
- ❌ **Deep Linking**: No deep link handling yet → cannot share links to specific goals
- ❌ **State Restoration**: Goal name passed as argument → not preserved on process death (needs SavedStateHandle)
- ⚠️ **Back Stack**: "session" is start destination but Dashboard navigates to Session → creates circular back stack

**Recommended Migration Path:**
```kotlin
// Type-safe navigation with Kotlin Serialization
@Serializable object SessionRoute
@Serializable object DashboardRoute
@Serializable data class GoalDetailRoute(val goalName: String)

NavHost(navController, startDestination = SessionRoute) {
    composable<SessionRoute> { SessionScreen(...) }
    composable<DashboardRoute> { DashboardScreen(...) }
    composable<GoalDetailRoute> { backStackEntry ->
        val args: GoalDetailRoute = backStackEntry.toRoute()
        GoalDetailScreen(goal = args.goalName)
    }
}
```

---

## ⚡ Concurrency & Threading Model

### Coroutine Architecture

**Dispatcher Strategy (Assumed):**
```kotlin
// ViewModels - Main dispatcher (default)
viewModelScope.launch { 
    // Runs on Main (UI thread safe)
    repository.getData() // Suspends, switches dispatcher internally
}

// Repositories - IO dispatcher for database operations
withContext(Dispatchers.IO) {
    sessionDao.insertSession(entity)
}

// Use Cases - Inherit caller's context (no dispatcher switching)
suspend operator fun invoke(): Result<Data> {
    // Pure computation on caller's dispatcher
    return repository.getData() // Repository handles IO switch
}
```

**Thread Safety Mechanisms:**
1. **Room**: DAO suspend functions automatically run on background thread
2. **StateFlow**: Updates serialized, thread-safe by design
3. **viewModelScope**: Tied to ViewModel lifecycle, cancels on clear
4. **Flow Operators**: All operators respect structured concurrency

**Concurrency Patterns Observed:**
```kotlin
// Pattern 1: Sequential async operations
viewModelScope.launch {
    _uiState.value = Loading
    val stats = getTodayStatsUseCase()        // Suspends, waits
    val sessions = getRecentSessionsUseCase() // Suspends, waits
    _uiState.value = Success(stats, sessions)
}

// Pattern 2: Parallel async operations (potential optimization)
viewModelScope.launch {
    _uiState.value = Loading
    val (stats, sessions) = awaitAll(
        async { getTodayStatsUseCase() },
        async { getRecentSessionsUseCase() }
    )
    _uiState.value = Success(stats, sessions)
}

// Pattern 3: Reactive streams (Flow)
repository.observeActiveSession()
    .map { session -> session?.let { UiState.Active(it) } ?: UiState.Idle }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Idle)
```

**Lifecycle Integration:**
- **Compose collectAsState()**: Subscribes in composition, unsubscribes on disposal → no leaks
- **viewModelScope**: Survives configuration changes, cancels on ViewModel clear
- **WhileSubscribed(5000ms)**: Keeps upstream active for 5s after last subscriber → optimizes for config changes

---

## 🚀 Setup & Run Instructions

### Prerequisites

- **JDK**: 17 or higher
- **Android Studio**: Hedgehog (2023.1.1) or later
- **Android SDK**: API 33+ (Android 13)

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd deep_work_tracker_mobile_app
   ```

2. **Open in Android Studio**
   - File → Open → Select project directory
   - Wait for Gradle sync to complete

3. **Sync Gradle dependencies**
   ```bash
   ./gradlew build --refresh-dependencies
   ```

4. **Run the app**
   - Connect a physical device (API 33+) or start an emulator
   - Click ▶️ Run or:
     ```bash
     ./gradlew installDebug
     ```

5. **Run tests**
   ```bash
   # Unit tests
   ./gradlew test
   
   # Instrumented tests (requires connected device)
   ./gradlew connectedAndroidTest
   ```

### Build Variants

- **Debug**: Development build with logging enabled
- **Release**: Production build with ProGuard/R8 optimization (TBD)

---

## 🧪 Testing Architecture

### Test Pyramid Strategy

```
                     ┌──────────────┐
                     │   E2E Tests  │  ← Compose UI Tests (few)
                     │ (Slow/Flaky) │
                  ┌──┴──────────────┴──┐
                  │  Integration Tests  │  ← Repository + Room (some)
                  │    (Medium Speed)   │
            ┌─────┴─────────────────────┴─────┐
            │        Unit Tests                │  ← Use Cases, ViewModels (many)
            │      (Fast, Isolated)            │
            └──────────────────────────────────┘
```

**Testing Layers (Inferred from Dependencies):**

**1. Unit Tests (`:core:domain`, ViewModels)**
```kotlin
// Use Case Tests (Pure Kotlin, no Android)
class GetTodayStatsUseCaseTest {
    private val mockRepository = mockk<SessionRepository>()
    private val useCase = GetTodayStatsUseCase(mockRepository)
    
    @Test
    fun `should calculate stats from today's sessions`() = runTest {
        // Given
        coEvery { mockRepository.getSessionsByDate(any()) } returns flowOf(fakeSessions)
        
        // When
        val result = useCase()
        
        // Then
        assertThat(result).isInstanceOf<Result.Success>()
        assertThat(result.data.totalFocusTime).isEqualTo(7200000L) // 2 hours
    }
}

// ViewModel Tests (with Turbine for Flow testing)
class DashboardViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()
    
    private val mockUseCase = mockk<GetTodayStatsUseCase>()
    private lateinit var viewModel: DashboardViewModel
    
    @Test
    fun `should emit loading then success state`() = runTest {
        // Given
        coEvery { mockUseCase() } returns Result.Success(fakeStats)
        
        // When
        viewModel = DashboardViewModel(mockUseCase)
        
        // Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf<Loading>()
            assertThat(awaitItem()).isEqualTo(Success(fakeStats))
        }
    }
}
```

**2. Integration Tests (Repositories + Room)**
```kotlin
// Repository Tests (with in-memory Room database)
@RunWith(AndroidJUnit4::class)
class SessionRepositoryImplTest {
    private lateinit var database: DeepWorkDatabase
    private lateinit var repository: SessionRepositoryImpl
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DeepWorkDatabase::class.java
        ).build()
        repository = SessionRepositoryImpl(database.sessionDao(), mapper)
    }
    
    @Test
    fun `should save and retrieve session`() = runTest {
        // Given
        val session = FocusSession(id = "1", goal = "Code", ...)
        
        // When
        repository.saveSession(session)
        val retrieved = repository.getSessionById("1")
        
        // Then
        assertThat(retrieved).isEqualTo(session)
    }
}
```

**3. UI Tests (Compose Testing)**
```kotlin
@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {
    @get:Rule val composeTestRule = createComposeRule()
    
    @Test
    fun `should display today stats`() {
        // Given
        val fakeState = DashboardUiState(
            todayStats = TodayStats(sessionCount = 3, totalFocusTime = 7200000L),
            isLoading = false
        )
        
        // When
        composeTestRule.setContent {
            DashboardScreen(viewModel = FakeViewModel(fakeState))
        }
        
        // Then
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
        composeTestRule.onNodeWithText("2h 0m").assertIsDisplayed()
    }
}
```

**Testing Dependencies Available:**
- **JUnit 4**: Standard test runner
- **MockK**: Kotlin-friendly mocking (supports coroutines, suspend functions)
- **Turbine**: Flow testing library (simplifies `StateFlow` assertions)
- **Truth**: Fluent assertion library (better readability than JUnit asserts)
- **kotlinx-coroutines-test**: Test dispatchers, `runTest` for coroutine testing
- **Compose UI Test**: Semantic tree-based UI testing (no Espresso)

---

## 🚨 Error Handling Strategy

### Domain Error Hierarchy

```kotlin
// Assumed domain error structure (from docs)
sealed class DeepWorkError(val message: String) {
    data class DatabaseError(override val message: String) : DeepWorkError(message)
    data class ValidationError(override val message: String) : DeepWorkError(message)
    data class NotFoundError(val entityId: String) : DeepWorkError("Entity not found: $entityId")
    data class UnknownError(override val message: String) : DeepWorkError(message)
}

// Result wrapper (from :core:common)
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: DeepWorkError) : Result<Nothing>()
    
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }
}
```

**Error Propagation Pattern:**
```kotlin
// Data Layer: Catch low-level exceptions, convert to domain errors
override suspend fun saveSession(session: FocusSession): Result<Unit> {
    return try {
        sessionDao.insertSession(mapper.toEntity(session))
        Result.Success(Unit)
    } catch (e: SQLiteConstraintException) {
        Result.Error(DeepWorkError.ValidationError("Duplicate session ID"))
    } catch (e: Exception) {
        Result.Error(DeepWorkError.DatabaseError(e.message ?: "Unknown DB error"))
    }
}

// Domain Layer: Use Cases propagate Result<T>
suspend operator fun invoke(goal: String): Result<FocusSession> {
    if (goal.isBlank()) {
        return Result.Error(DeepWorkError.ValidationError("Goal cannot be empty"))
    }
    return repository.saveSession(createSession(goal))
}

// Presentation Layer: ViewModels convert errors to UI states
when (val result = useCase(goal)) {
    is Result.Success -> _uiState.value = UiState.Success(result.data)
    is Result.Error -> _uiState.value = UiState.Error(result.exception.message)
}
```

**Benefits of This Approach:**
1. **Type Safety**: Errors are typed, not string messages → exhaustive `when` handling
2. **Layer Isolation**: Low-level exceptions don't leak to presentation
3. **Testability**: Errors can be mocked and asserted in tests
4. **User Messaging**: Error types map to user-friendly messages in UI layer

**Current Limitations (Opportunities for Improvement):**
- ❌ **No Retry Logic**: Transient failures (e.g., disk full) not automatically retried
- ❌ **No Error Recovery**: Users cannot retry failed actions from error UI
- ⚠️ **Generic Error Messages**: `UnknownError` loses context → should log stack traces
- ⚠️ **No Analytics**: Errors not tracked → cannot identify common failure modes

---

## 💪 Strengths of Current Design

### 1. **Modular Architecture**
- Clear separation of concerns across modules
- Parallel compilation for faster build times
- Easy to onboard new features without touching existing code

### 2. **Testability**
- Pure Kotlin domain layer enables fast unit tests without Android framework
- Repository interfaces allow easy mocking in ViewModels
- Use Cases encapsulate testable business logic

### 3. **Type Safety & Null Safety**
- Kotlin's type system prevents common runtime errors
- `Result<T>` wrapper for explicit error handling
- Domain models enforce business rules at compile time

### 4. **Reactive UI**
- Compose's declarative paradigm eliminates manual view updates
- StateFlow ensures UI always reflects latest state
- No lifecycle management bugs (no `Fragment` transactions, no view binding leaks)

### 5. **Offline-First Design**
- Room database as single source of truth
- No network dependency for core features
- Fast, responsive UX without loading spinners

### 6. **Dependency Injection**
- Hilt eliminates boilerplate DI code
- Compile-time safety for dependency graph
- Easy to swap implementations for testing

### 7. **Modern Tech Stack**
- Latest stable Kotlin (2.0.21) with performance improvements
- Compose UI toolkit reduces view inflation overhead
- Coroutines + Flow for efficient async operations

### 8. **Consistent Naming Conventions**
- Use Cases named as operations: `GetTodayStatsUseCase`, `GetRecentSessionsUseCase`, `GetAllSessionUseCase`, `StartSessionUseCase`
- Repository methods follow CRUD vocabulary: `getSessionById`, `saveSession`, `deleteSession`, `getAllSessions`
- UI State classes colocated with ViewModels: `DashboardUiState`, `SessionUiState`

### 9. **Scalable Module Structure**
- Feature modules can be developed in parallel by different teams
- Core modules enforce shared contracts → reduces merge conflicts
- Future features (insights, notifications) can be added as new `:feature` modules without touching existing code

---

## ⚖️ Architectural Trade-offs & Decisions

### Trade-off Analysis

| Decision | Benefits | Costs | Rationale |
|----------|----------|-------|-----------|
| **Clean Architecture** | Testable domain logic, swappable data sources, clear boundaries | More boilerplate, steeper learning curve | Prioritizes long-term maintainability over initial velocity |
| **Multi-Module Setup** | Parallel builds, clear dependencies, enforced separation | Slower IDE indexing, complex Gradle setup | Worth it for 3+ developers, future scalability |
| **Compose-Only (No XML)** | Declarative UI, less boilerplate, better type safety | Smaller community resources, occasional framework bugs | Future of Android UI, worth early adoption |
| **Room (No Realm/SQL)** | Type-safe queries, compile-time verification, official support | Learning curve for complex queries | Standard choice for Android persistence |
| **StateFlow (No LiveData)** | Better coroutine integration, more operators, multiplatform-ready | Requires `collectAsState()` in Compose | LiveData is legacy, StateFlow is future |
| **Hilt (No Koin/Manual DI)** | Compile-time safety, performance, Google-maintained | More complex setup, annotation processing | Best for medium-large projects with multiple modules |
| **Single Activity** | Simpler navigation, no Fragment transactions | Complex back stack management | Compose best practice, eliminates Fragment lifecycle bugs |
| **Offline-First (No Backend Yet)** | Fast, reliable, privacy-focused | No cross-device sync, no cloud backup | Aligns with product vision (privacy, self-hosted data) |

### When This Architecture Makes Sense

✅ **Ideal For:**
- Apps with complex business logic (not just CRUD)
- Teams of 3+ developers working in parallel
- Long-lived projects (2+ years maintenance)
- Apps requiring extensive unit testing
- Projects planning future backend integration

❌ **Overkill For:**
- Simple CRUD apps with 5-10 screens
- Solo developer prototypes with <3 month timeline
- Apps with no business logic (pure UI wrappers)
- Projects with extreme time constraints

### Alternative Architectures Considered (Assumed)

| Alternative | Why Not Chosen |
|-------------|----------------|
| **MVI (Model-View-Intent)** | More boilerplate than MVVM, less familiar to Android devs |
| **Redux/Unidirectional** | Too much ceremony for this app's complexity level |
| **Single-Module Monolith** | Doesn't scale for 3+ features, slower builds |
| **Feature-First (No Core)** | Code duplication across features, inconsistent patterns |
| **VIPER/RIBs** | Over-engineered for mobile, more suited to large-scale apps |

---

## 🎯 Potential Improvements & Next Steps

### Performance Optimization Opportunities

**Database Query Optimization:**
```sql
-- Add composite indexes for common queries
CREATE INDEX idx_sessions_date_goal ON sessions(start_time DESC, goal);
CREATE INDEX idx_sessions_active ON sessions(is_active) WHERE is_active = 1;

-- Optimize aggregation queries with materialized views (Room 2.6+)
CREATE VIEW daily_stats AS
SELECT 
    date(start_time/1000, 'unixepoch', 'localtime') as date,
    COUNT(*) as session_count,
    SUM(total_duration) as total_duration,
    AVG(total_duration) as avg_duration
FROM sessions
GROUP BY date;
```

**Pagination Strategy:**
```kotlin
// Current: Loads all 30 days of sessions into memory
fun getSessionsByDateRange(start: LocalDate, end: LocalDate): Flow<List<FocusSession>>

// Improved: Paging 3 for infinite scroll
@Query("SELECT * FROM sessions ORDER BY start_time DESC")
fun getSessionsPaged(): PagingSource<Int, SessionEntity>

// In ViewModel:
val sessionsPager = Pager(PagingConfig(pageSize = 20)) {
    repository.getSessionsPaged()
}.flow.cachedIn(viewModelScope)
```

**Compose Performance:**
```kotlin
// Issue: Chart recomposes on every state update
@Composable
fun GoalDistributionChart(sessions: List<FocusSession>) {
    val goalDistribution = remember(sessions) { /* expensive calculation */ }
}

// Improvement: Derive state in ViewModel to avoid recalculation
data class DashboardUiState(
    val recentSessions: List<FocusSession>,
    val goalDistribution: List<GoalData> // Pre-calculated
)

// Further optimization: Use derivedStateOf for expensive computations
val sortedSessions by remember {
    derivedStateOf { sessions.sortedByDescending { it.startTime } }
}
```

**Memory Management:**
- ⚠️ **Current**: Loading 30 days of session history → could be 1000+ sessions for power users
- ✅ **Recommendation**: Limit to 100 sessions + "Load More" button, or implement virtual scrolling

### Short-Term Engineering Improvements

1. **Error Handling Enhancement**
   - Implement retry logic for transient failures (exponential backoff)
   - Add user-facing error messages with actionable guidance ("Check storage space", "Try again")
   - Introduce `ErrorState` in UiState for granular error UI (snackbar vs dialog vs inline)
   - Log errors to Timber with stack traces for debugging

2. **Navigation Refinement**
   - Migrate to type-safe Navigation Compose arguments (Kotlin Serialization)
   - Add deep linking support: `deepworktracker://goal/{goalName}`
   - Implement SavedStateHandle for goal parameter (survives process death)
   - Fix circular back stack (Session ↔ Dashboard infinite loop)

3. **UI/UX Polish**
   - Add loading skeletons for async data (Placeholder API)
   - Implement pull-to-refresh on Dashboard (SwipeRefresh)
   - Add empty state illustrations (Material Icons Extended)
   - Support dynamic color (Material You)
   - Implement dark mode (already supported by Material 3, likely needs testing)

4. **Performance Optimization**
   - Add database indexes: `idx_sessions_date_goal`, `idx_sessions_active`
   - Implement pagination for session lists (Paging 3)
   - Profile Compose recompositions with Layout Inspector
   - Use `derivedStateOf` for expensive UI computations
   - Enable R8 full mode for release builds

5. **Testing Coverage**
   - Add ViewModel unit tests with Turbine for Flow testing (target: 80% coverage)
   - Write repository integration tests with in-memory Room DB
   - Implement UI tests with Compose Testing library (critical paths: start session, view stats)
   - Add screenshot tests with Paparazzi (prevent UI regressions)

### Medium-Term Feature Development

6. **Interruption Tracking**
   - Implement lifecycle observers for app backgrounding detection
   - Add screen lock detection via BroadcastReceiver
   - Store interruption events in database with timestamps

7. **Advanced Analytics**
   - Weekly/monthly aggregation views
   - Streak tracking (consecutive focus days)
   - Goal comparison charts (time allocation trends)

8. **Insight Engine (Rule-Based)**
   - Detect patterns: "You focus best between 9-11 AM"
   - Identify declining trends: "Focus time dropped 20% this week"
   - Suggest optimal session durations based on historical data

9. **Background Work**
   - Schedule daily stats aggregation with WorkManager
   - Generate insights asynchronously
   - Send smart notifications based on patterns

### Long-Term Architectural Considerations

10. **Data Export & Backup**
    ```kotlin
    // Export Service (new :core:export module)
    interface ExportService {
        suspend fun exportToCSV(): Result<File>
        suspend fun exportToJSON(): Result<String>
        suspend fun importFromBackup(file: File): Result<Unit>
    }
    
    // CSV Export Implementation
    class CSVExportService @Inject constructor(
        private val sessionRepository: SessionRepository
    ) : ExportService {
        override suspend fun exportToCSV(): Result<File> {
            val sessions = sessionRepository.getAllSessions()
            val csvContent = sessions.joinToString("\n") { session ->
                "${session.id},${session.goal},${session.startTime},${session.totalDuration}"
            }
            return File(context.cacheDir, "sessions_export.csv")
                .apply { writeText("id,goal,start_time,duration\n$csvContent") }
                .let { Result.Success(it) }
        }
    }
    ```
    **Considerations:**
    - GDPR compliance: Right to deletion, right to data portability
    - Encryption at rest for sensitive data (Android Keystore)
    - Cloud backup (optional): Encrypted upload to user's Google Drive/iCloud

11. **Multi-Platform Expansion (Kotlin Multiplatform)**
    ```
    shared/
    ├── commonMain/
    │   ├── domain/          # Pure Kotlin (already done!)
    │   ├── data/            # SQLDelight instead of Room
    │   └── common/          # Already portable
    ├── androidMain/         # Android-specific (Hilt, Room)
    ├── iosMain/             # iOS-specific (SwiftUI interop)
    └── desktopMain/         # Compose Desktop
    ```
    **Migration Path:**
    1. Domain layer is already pure Kotlin → zero changes needed
    2. Swap Room → SQLDelight (multiplatform SQL)
    3. Create platform-specific repository implementations
    4. Share ViewModels across platforms (Compose Multiplatform)
    
    **Estimated Effort:** 2-3 weeks for iOS/Desktop ports after refactor

12. **ML-Powered Insights (On-Device AI)**
    ```kotlin
    // ML Model Integration (TensorFlow Lite)
    class FocusPatternPredictor @Inject constructor() {
        private val interpreter = Interpreter(loadModelFile())
        
        fun predictOptimalTime(historicalData: List<FocusSession>): LocalTime {
            val features = extractFeatures(historicalData) // [hour, day_of_week, duration]
            val output = FloatArray(24) // Probability distribution over hours
            interpreter.run(features, output)
            return output.indices.maxBy { output[it] }.toLocalTime()
        }
    }
    ```
    **Training Pipeline:**
    1. Aggregate anonymized user data (opt-in)
    2. Train model offline: features = [hour, weekday, duration] → label = success_rate
    3. Export to TFLite, ship with app
    4. Periodically retrain with new data (federated learning)
    
    **Privacy Guarantees:**
    - On-device inference only (no cloud API calls)
    - No PII in training data (just timestamps + durations)
    - User can disable AI features entirely

13. **Scalability Enhancements**
    
    **Database Scaling:**
    ```kotlin
    // Problem: 1 year of data = 10,000+ sessions → slow queries
    // Solution 1: Archival strategy
    @Query("DELETE FROM sessions WHERE start_time < :cutoffDate")
    suspend fun archiveOldSessions(cutoffDate: Long)
    
    // Solution 2: Aggregated tables
    CREATE TABLE monthly_stats (
        year_month TEXT PRIMARY KEY,  -- "2024-01"
        total_sessions INTEGER,
        total_duration INTEGER,
        top_goal TEXT
    );
    
    // Solution 3: Partition by year (SQLite 3.38+)
    CREATE TABLE sessions_2024 AS SELECT * FROM sessions WHERE year = 2024;
    ```
    
    **App Size Optimization:**
    - R8 full mode → ~30% APK size reduction
    - Dynamic feature modules (`:feature:insights` loaded on-demand)
    - WebP for images (already used in project)
    - Asset shrinking + resource minification
    
    **Monitoring & Observability:**
    ```kotlin
    // Crash Reporting (Firebase Crashlytics)
    class CrashlyticsErrorTracker @Inject constructor(
        private val crashlytics: FirebaseCrashlytics
    ) : ErrorTracker {
        override fun logError(error: DeepWorkError) {
            crashlytics.recordException(Exception(error.message))
            crashlytics.setCustomKey("error_type", error::class.simpleName ?: "Unknown")
        }
    }
    
    // Analytics (Privacy-Safe)
    interface AnalyticsTracker {
        fun trackSessionStarted(goal: String)  // Hash goal name
        fun trackFeatureUsed(featureName: String)
        fun trackError(errorType: String)
    }
    ```
    **Privacy-Safe Analytics:**
    - No user identifiers (device ID, email, etc.)
    - Aggregate metrics only (daily active users, feature usage %)
    - Opt-out mechanism in settings
    - Self-hosted analytics server (Plausible, Matomo)

---

## 📊 Technical Maturity Assessment

### Current State (Estimated)

| Dimension | Maturity Level | Evidence | Next Milestone |
|-----------|---------------|----------|----------------|
| **Architecture** | 🟢 Production-Ready | Clean Architecture, MVVM, clear boundaries | Add integration tests |
| **Code Quality** | 🟡 Good | Type-safe, Kotlin best practices | Add linter rules (detekt) |
| **Testing** | 🔴 Early | Test dependencies present, likely low coverage | Achieve 80% coverage |
| **Performance** | 🟡 Good | Efficient for current scale (<10k sessions) | Add database indexes |
| **Error Handling** | 🟡 Good | Result<T> wrapper, typed errors | Add retry logic + user recovery |
| **Security** | 🟡 Good | Offline-first, no network vulnerabilities | Add data encryption |
| **Observability** | 🔴 None | No crash reporting or analytics | Add Firebase Crashlytics |
| **Documentation** | 🟢 Excellent | Comprehensive docs, clear README | Keep updated as features evolve |
| **Deployment** | 🔴 Unknown | No CI/CD pipeline visible | Setup GitHub Actions |

### Production Readiness Checklist

**Must-Have Before 1.0 Launch:**
- ✅ Core features functional (Session tracking, Dashboard)
- ✅ Clean architecture implemented
- ⚠️ Error handling (partial - needs user-facing recovery)
- ❌ Crash reporting (not implemented)
- ❌ Unit test coverage >70%
- ❌ UI tests for critical paths
- ❌ ProGuard/R8 rules for release build
- ❌ Privacy policy + data handling docs
- ❌ Accessibility (TalkBack support, content descriptions)
- ❌ Localization (i18n for multiple languages)

**Nice-to-Have:**
- Performance profiling (baseline metrics)
- A/B testing infrastructure (Firebase Remote Config)
- Onboarding flow for new users
- App shortcuts (long-press app icon → "Start Session")
- Widget support (home screen focus timer)

### Code Health Metrics (Estimated)

```
Lines of Code (LoC):     ~5,000 (moderate size)
Modules:                 7 (scalable structure)
External Dependencies:   ~25 (reasonable for Android)
Cyclomatic Complexity:   Likely low (clean architecture → simple methods)
Test Coverage:           Unknown (likely <30%)
Build Time:              <2 min (fast for current size)
APK Size (Debug):        ~15 MB (typical for Compose app)
Min SDK:                 API 33 (limits audience to Android 13+)
```

**Recommended Tooling:**
- **Static Analysis**: detekt (Kotlin linter), Android Lint
- **Code Coverage**: JaCoCo (track test coverage over time)
- **Dependency Updates**: Gradle Versions Plugin
- **CI/CD**: GitHub Actions (run tests on every PR)
- **Release Management**: Fastlane (automate Play Store uploads)

---

## 🔍 Technical Assumptions & Constraints

### Explicit Assumptions Made in This README

**Architecture Inference:**
1. **Use Cases as Operators**: Assumed based on Kotlin convention (`operator fun invoke()`)
2. **Hilt Scoping**: Inferred `@Singleton` for repositories, `@ViewModelScoped` for use cases
3. **DAO Suspend Functions**: Assumed Room best practices (DAOs return `suspend` or `Flow`)
4. **Entity Mapping**: Assumed mapper pattern exists based on `:core:data/mapper/` directory
5. **Result Wrapper**: Confirmed from `:core:common` but implementation details inferred
6. **StateFlow Pattern**: Observed in ViewModel files, assumed consistent across project

**Database Schema:**
- **Assumed Tables**: `sessions`, `interruptions`, `insights` (inferred from domain models)
- **Assumed Indexes**: None explicitly defined → recommended additions based on query patterns
- **Assumed Constraints**: Foreign keys on `interruptions.session_id`, unique constraint on `sessions.id`
- **Type Converters**: Assumed for `Instant`, `LocalDate`, `Duration` (Room doesn't support these natively)

**Navigation:**
- **String-Based Routes**: Confirmed from `MainActivity.kt`
- **No Deep Linking**: Assumed absent (no `NavDeepLink` annotations observed)
- **Single NavHost**: Assumed based on single-activity pattern

**Testing:**
- **Coverage Estimate**: Based on dependency presence (MockK, Turbine), not actual metrics
- **Test Structure**: Inferred from Android best practices, not actual test files

**Performance:**
- **Query Complexity**: Estimated based on likely SQL for dashboard stats
- **Memory Usage**: Calculated assuming 1KB per session × 1000 sessions = ~1MB
- **Recomposition**: Inferred potential issues from Compose patterns

### Known Constraints

**Platform Constraints:**
- **Min SDK 33**: Limits to ~30% of Android devices (as of 2024)
- **Single Language**: Likely no i18n yet (no `strings.xml` variants observed)
- **Portrait Only**: Assumed no landscape support (common for focus apps)

**Technical Debt (Inferred):**
- **No Proguard Rules**: Release builds likely to crash without rules for Room, Hilt, Serialization
- **No Migration Strategy**: Room database version likely 1 → breaking schema changes will lose data
- **No Backup Agent**: Android Auto Backup disabled or unconfigured → users lose data on device wipe
- **No Accessibility**: Likely missing content descriptions for screen readers

**Business Constraints:**
- **Offline-Only**: No sync → cannot track across devices
- **Local-Only**: No social features, no leaderboards
- **Privacy-First**: No analytics → harder to identify user pain points

---

## 📝 Architectural Decision Log (ADL)

### ADR-001: Clean Architecture Over MVC/MVP
**Date**: Initial project setup  
**Status**: Accepted  
**Context**: Need architecture that scales to 10+ features, supports testing  
**Decision**: Implement Clean Architecture with 3 layers (Presentation/Domain/Data)  
**Consequences**: +Testability +Maintainability -Initial complexity -Boilerplate

### ADR-002: Jetpack Compose Over XML Views
**Date**: Initial project setup  
**Status**: Accepted  
**Context**: Need modern UI toolkit, reduce view boilerplate  
**Decision**: Use Compose exclusively, no XML layouts  
**Consequences**: +Declarative UI +Type safety -Smaller community -Occasional framework bugs

### ADR-003: Hilt Over Koin/Manual DI
**Date**: Initial project setup  
**Status**: Accepted  
**Context**: Need DI framework for multi-module project  
**Decision**: Use Hilt for compile-time DI  
**Consequences**: +Compile-time safety +Performance -Complex setup -Annotation processing time

### ADR-004: Room Over Realm/SQLDelight
**Date**: Initial project setup  
**Status**: Accepted  
**Context**: Need local database for session tracking  
**Decision**: Use Room (official Jetpack library)  
**Consequences**: +Type-safe queries +Official support +Migration tools -Kotlin Multiplatform limited

### ADR-005: StateFlow Over LiveData
**Date**: Initial project setup  
**Status**: Accepted  
**Context**: Need reactive state management in ViewModels  
**Decision**: Use StateFlow instead of LiveData  
**Consequences**: +Coroutine integration +Multiplatform-ready +More operators -Requires `.collectAsState()` in Compose

### ADR-006: Multi-Module Over Monolith
**Date**: Initial project setup  
**Status**: Accepted  
**Context**: Plan for 5+ features, need team scalability  
**Decision**: Split into `:core` and `:feature` modules  
**Consequences**: +Parallel builds +Clear boundaries +Team scalability -Slower IDE indexing -More Gradle config

### ADR-007: String-Based Navigation Over Type-Safe (Current)
**Date**: Navigation implementation  
**Status**: Accepted (To Be Revisited)  
**Context**: Need basic navigation between screens  
**Decision**: Use string routes with manual encoding  
**Consequences**: +Simple setup +Quick implementation -Type safety -Runtime errors possible  
**Future**: Migrate to Kotlin Serialization-based navigation in Navigation Compose 2.8+

### ADR-008: Offline-First, No Backend (Phase 1)
**Date**: Initial product scope  
**Status**: Accepted  
**Context**: MVP focus on local tracking, privacy concerns  
**Decision**: Build offline-first with Room only, defer backend  
**Consequences**: +Privacy +Fast UX +Simpler architecture -No cross-device sync -No cloud backup

---

## 📚 Additional Documentation

- **[ARCHITECTURE_ANALYSIS.md](./ARCHITECTURE_ANALYSIS.md)**: Deep dive into architectural decisions, product vision, and system design
- **[TECHNICAL_SPECIFICATION.md](./TECHNICAL_SPECIFICATION.md)**: Detailed technical specs including data models, API design, and testing strategy
- **[IMPLEMENTATION_ROADMAP.md](./IMPLEMENTATION_ROADMAP.md)**: Phased development plan with milestones
- **[ARCHITECTURE_AND_BASE_LIBS.md](./ARCHITECTURE_AND_BASE_LIBS.md)**: Architecture patterns and base library usage guide

---

## 🤝 Contributing

(To be defined: contribution guidelines, code style, PR process)

---

## 📄 License

(To be defined)

---

## 📧 Contact

(To be defined)

---

## 📖 Glossary

**Architectural Terms:**
- **Clean Architecture**: Layered architecture pattern that enforces dependency inversion and separation of concerns
- **MVVM**: Model-View-ViewModel pattern where ViewModel mediates between UI and business logic
- **UDF**: Unidirectional Data Flow - data flows in one direction (User Action → State Update → UI Render)
- **SSOT**: Single Source of Truth - one authoritative data source (Room database in this app)
- **Repository Pattern**: Abstraction over data sources, provides clean API for data operations

**Technical Terms:**
- **StateFlow**: Hot coroutine Flow that emits current state and subsequent updates to subscribers
- **Compose**: Android's modern declarative UI toolkit (replaces XML views)
- **Room**: Android's SQLite ORM with compile-time query verification
- **Hilt**: Dependency injection framework built on Dagger, optimized for Android
- **Use Case**: Single-responsibility class that encapsulates one business operation

**Domain Terms:**
- **Focus Session**: A timed work session with a specific goal
- **Interruption**: An event that breaks focus (app backgrounded, screen locked, etc.)
- **Insight**: AI-generated or rule-based observation about focus patterns
- **Deep Work**: Distraction-free concentration on cognitively demanding tasks

---

## 🎓 For New Developers

**Getting Up to Speed:**

1. **Start Here** (1 hour):
   - Read [ARCHITECTURE_ANALYSIS.md](./ARCHITECTURE_ANALYSIS.md) for product vision
   - Review this README's "Architecture & Design" section
   - Skim [TECHNICAL_SPECIFICATION.md](./TECHNICAL_SPECIFICATION.md)

2. **Explore Codebase** (2 hours):
   - Open `MainActivity.kt` to understand navigation flow
   - Read `DashboardViewModel.kt` to see MVVM + UDF pattern
   - Examine `SessionRepository.kt` (interface) → `SessionRepositoryImpl.kt` (implementation)
   - Check `FocusSession.kt` domain model

3. **Make First Change** (2 hours):
   - Pick a small task from "Short-Term Improvements"
   - Write unit tests first (TDD approach)
   - Open PR with clear description

4. **Deep Dive** (ongoing):
   - Read [Compose documentation](https://developer.android.com/jetpack/compose)
   - Study [Clean Architecture in Android](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
   - Review [Kotlin Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html)

**Key Files to Understand:**

| Priority | File | Why Important |
|----------|------|---------------|
| 🔴 High | `MainActivity.kt` | Navigation graph, app entry point |
| 🔴 High | `DashboardViewModel.kt` | MVVM pattern, state management |
| 🔴 High | `FocusSession.kt` | Core domain model |
| 🟡 Medium | `SessionRepositoryImpl.kt` | Data layer implementation |
| 🟡 Medium | `DashboardScreen.kt` | Compose UI patterns |
| 🟢 Low | `build.gradle.kts` | Dependency management |

---

## 🤝 Contributing

**Code Style:**
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation (no tabs)
- Max line length: 120 characters
- Run `./gradlew detekt` before committing (if configured)

**Commit Messages:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`  
**Example**: `feat(dashboard): add goal detail screen with 30-day history`

**Pull Request Process:**
1. Create feature branch: `git checkout -b feat/your-feature`
2. Write tests first (TDD)
3. Implement feature
4. Ensure tests pass: `./gradlew test`
5. Open PR with clear description and screenshots (for UI changes)
6. Wait for code review approval
7. Squash and merge

---

## 📧 Contact & Support

**Project Maintainer**: (To be defined)  
**Issue Tracker**: (To be defined - likely GitHub Issues)  
**Discussion Forum**: (To be defined - likely GitHub Discussions)

---

## 📄 License

(To be defined - recommend Apache 2.0 or MIT for open source)

---

## 🙏 Acknowledgments

- **Clean Architecture**: Uncle Bob's original concept
- **Jetpack Compose**: Google's Android UI team
- **Vico Charts**: Patryk Michalik's charting library
- **Kotlin**: JetBrains for the amazing language

---

**Built with precision engineering using Clean Architecture, Jetpack Compose, and Kotlin**

*README Last Updated: January 2026*  
*Architecture Version: 1.0*  
*Target Audience: Senior Android Engineers, Technical Leads, Staff Engineers*
