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

## 📚 Additional Documentation

- **[ARCHITECTURE_ANALYSIS.md](./ARCHITECTURE_ANALYSIS.md)**: Deep dive into architectural decisions, product vision, and system design
- **[TECHNICAL_SPECIFICATION.md](./TECHNICAL_SPECIFICATION.md)**: Detailed technical specs including data models, API design, and testing strategy
- **[IMPLEMENTATION_ROADMAP.md](./IMPLEMENTATION_ROADMAP.md)**: Phased development plan with milestones
- **[ARCHITECTURE_AND_BASE_LIBS.md](./ARCHITECTURE_AND_BASE_LIBS.md)**: Architecture patterns and base library usage guide

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

**Built with precision engineering using Clean Architecture, Jetpack Compose, and Kotlin**

*README Last Updated: January 2026*  
*Architecture Version: 1.0*  
*Target Audience: Senior Android Engineers, Technical Leads, Staff Engineers*
