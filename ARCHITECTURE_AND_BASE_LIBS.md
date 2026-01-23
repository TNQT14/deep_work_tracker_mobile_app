# Deep Work Tracker - Kiến Trúc & Base Libraries

## 📋 Mục Lục
1. [Kiến Trúc Tổng Quan](#1-kiến-trúc-tổng-quan)
2. [Module Structure Chi Tiết](#2-module-structure-chi-tiết)
3. [Base Libraries & Dependencies](#3-base-libraries--dependencies)
4. [Architecture Patterns](#4-architecture-patterns)
5. [Code Organization](#5-code-organization)
6. [Best Practices](#6-best-practices)

---

## 1. Kiến Trúc Tổng Quan

### 1.1 Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Compose    │  │   ViewModel  │  │   Navigation │     │
│  │   UI         │  │   (MVVM)     │  │   Compose    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Use Cases  │  │   Entities   │  │   Repository │     │
│  │              │  │              │  │   Interface  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Repository  │  │   Room DB     │  │   Remote     │     │
│  │  Impl        │  │   (Local)     │  │   (Future)   │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                      Core Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Utils      │  │   Services   │  │   DI (Hilt)  │     │
│  │              │  │              │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Architecture Principles

1. **Clean Architecture**: Separation of concerns với 3 layers rõ ràng
2. **Dependency Rule**: Dependencies chỉ point inward (Presentation → Domain → Data)
3. **Single Responsibility**: Mỗi class/module có một trách nhiệm duy nhất
4. **Dependency Injection**: Sử dụng Hilt cho DI
5. **Reactive Programming**: Flow/StateFlow cho data streams
6. **Testability**: Mỗi layer có thể test độc lập

---

## 2. Module Structure Chi Tiết

### 2.1 Module Hierarchy

```
deep_work_tracker/
├── app/                                    # Main application module
│   ├── src/main/
│   │   ├── java/com/deepworktracker/
│   │   │   ├── DeepWorkApplication.kt
│   │   │   ├── di/
│   │   │   │   ├── AppModule.kt
│   │   │   │   └── DatabaseModule.kt
│   │   │   └── MainActivity.kt
│   │   └── res/
│   └── build.gradle.kts
│
├── :feature:session/                       # Focus Session feature
│   ├── src/main/java/com/deepworktracker/session/
│   │   ├── presentation/
│   │   │   ├── SessionScreen.kt
│   │   │   ├── SessionViewModel.kt
│   │   │   └── SessionUiState.kt
│   │   ├── domain/
│   │   │   ├── usecase/
│   │   │   │   ├── StartSessionUseCase.kt
│   │   │   │   ├── EndSessionUseCase.kt
│   │   │   │   ├── PauseSessionUseCase.kt
│   │   │   │   └── ResumeSessionUseCase.kt
│   │   │   └── repository/
│   │   │       └── SessionRepository.kt (interface)
│   │   └── data/
│   │       ├── repository/
│   │       │   └── SessionRepositoryImpl.kt
│   │       └── local/
│   │           └── SessionDao.kt
│   └── build.gradle.kts
│
├── :feature:dashboard/                     # Dashboard feature
│   ├── src/main/java/com/deepworktracker/dashboard/
│   │   ├── presentation/
│   │   │   ├── DashboardScreen.kt
│   │   │   ├── DashboardViewModel.kt
│   │   │   └── DashboardUiState.kt
│   │   ├── domain/
│   │   │   └── usecase/
│   │   │       ├── GetDailyStatsUseCase.kt
│   │   │       └── GetWeeklyStatsUseCase.kt
│   │   └── data/
│   └── build.gradle.kts
│
├── :feature:insights/                     # Insights feature
│   ├── src/main/java/com/deepworktracker/insights/
│   │   ├── presentation/
│   │   │   ├── InsightsScreen.kt
│   │   │   └── InsightsViewModel.kt
│   │   ├── domain/
│   │   │   ├── usecase/
│   │   │   │   └── GenerateInsightUseCase.kt
│   │   │   └── rule/
│   │   │       ├── BestTimeWindowRule.kt
│   │   │       └── OptimalSessionLengthRule.kt
│   │   └── data/
│   └── build.gradle.kts
│
├── :feature:settings/                      # Settings feature
│   ├── src/main/java/com/deepworktracker/settings/
│   │   ├── presentation/
│   │   │   ├── SettingsScreen.kt
│   │   │   └── SettingsViewModel.kt
│   │   └── domain/
│   └── build.gradle.kts
│
├── :core:domain/                          # Domain layer (shared)
│   ├── src/main/java/com/deepworktracker/domain/
│   │   ├── model/
│   │   │   ├── FocusSession.kt
│   │   │   ├── Interruption.kt
│   │   │   ├── DailyStats.kt
│   │   │   └── Insight.kt
│   │   └── repository/
│   │       ├── SessionRepository.kt
│   │       ├── StatsRepository.kt
│   │       └── InsightRepository.kt
│   └── build.gradle.kts
│
├── :core:data/                            # Data layer (shared)
│   ├── src/main/java/com/deepworktracker/data/
│   │   ├── database/
│   │   │   ├── DeepWorkDatabase.kt
│   │   │   ├── entity/
│   │   │   │   ├── FocusSessionEntity.kt
│   │   │   │   ├── InterruptionEntity.kt
│   │   │   │   ├── DailyStatsEntity.kt
│   │   │   │   └── InsightEntity.kt
│   │   │   └── dao/
│   │   │       ├── SessionDao.kt
│   │   │       ├── InterruptionDao.kt
│   │   │       ├── StatsDao.kt
│   │   │       └── InsightDao.kt
│   │   ├── repository/
│   │   │   ├── SessionRepositoryImpl.kt
│   │   │   ├── StatsRepositoryImpl.kt
│   │   │   └── InsightRepositoryImpl.kt
│   │   └── mapper/
│   │       ├── SessionMapper.kt
│   │       └── StatsMapper.kt
│   └── build.gradle.kts
│
├── :core:common/                          # Common utilities
│   ├── src/main/java/com/deepworktracker/common/
│   │   ├── time/
│   │   │   ├── TimeFormatter.kt
│   │   │   └── TimeUtils.kt
│   │   ├── extension/
│   │   │   ├── DurationExtensions.kt
│   │   │   └── StringExtensions.kt
│   │   ├── di/
│   │   │   └── DispatcherModule.kt
│   │   └── result/
│   │       └── Result.kt
│   └── build.gradle.kts
│
├── :core:ui/                              # Common UI components
│   ├── src/main/java/com/deepworktracker/ui/
│   │   ├── theme/
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   └── component/
│   │       ├── TimerDisplay.kt
│   │       ├── StatCard.kt
│   │       └── InsightCard.kt
│   └── build.gradle.kts
│
└── build.gradle.kts                       # Root build file
```

### 2.2 Module Dependencies

```
app
├── depends on: :feature:session, :feature:dashboard, :feature:insights, :feature:settings
└── depends on: :core:ui, :core:common

:feature:session
├── depends on: :core:domain, :core:data, :core:ui, :core:common

:feature:dashboard
├── depends on: :core:domain, :core:data, :core:ui, :core:common

:feature:insights
├── depends on: :core:domain, :core:data, :core:ui, :core:common

:feature:settings
├── depends on: :core:domain, :core:ui, :core:common

:core:domain
└── no dependencies (pure Kotlin)

:core:data
└── depends on: :core:domain

:core:common
└── no dependencies (pure Kotlin)

:core:ui
└── depends on: Compose libraries
```

---

## 3. Base Libraries & Dependencies

### 3.1 Complete libs.versions.toml

```toml
[versions]
# Core Android
agp = "8.13.2"
kotlin = "2.0.21"
coreKtx = "1.17.0"

# Compose
composeBom = "2024.09.00"
lifecycleRuntimeKtx = "2.10.0"
activityCompose = "1.12.2"
lifecycleViewmodel = "2.10.0"
navigationCompose = "2.8.4"

# Dependency Injection
hilt = "2.51.1"
hiltNavigationCompose = "1.2.0"

# Database
room = "2.6.1"

# Coroutines
coroutines = "1.9.0"

# Work Manager
workManager = "2.9.1"

# Networking (Future)
ktor = "2.3.9"
ktorSerialization = "1.6.3"

# Serialization
kotlinxSerialization = "1.6.3"

# Logging
timber = "5.0.1"

# Testing
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
mockk = "1.13.10"
turbine = "1.1.0"
truth = "1.4.2"
robolectric = "4.13"

# Firebase (Optional - for crash reporting, analytics)
firebaseBom = "33.7.0"

# Charts/Visualization
vico = "1.13.1"  # Compose chart library

# Date/Time
kotlinxDatetime = "0.6.0"

# Permissions
accompanistPermissions = "0.36.0"

# Image Loading (if needed)
coil = "2.7.0"

[libraries]
# Core Android
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }

# Compose
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodel" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleViewmodel" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Room
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-paging = { group = "androidx.room", name = "room-paging", version.ref = "room" }

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }

# WorkManager
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }
androidx-work-testing = { group = "androidx.work", name = "work-testing", version.ref = "workManager" }

# Networking (Future)
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
ktor-client-content-negotiation = { group = "io.ktor", name = "ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { group = "io.ktor", name = "ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { group = "io.ktor", name = "ktor-client-logging", version.ref = "ktor" }

# Serialization
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# Logging
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
mockk-android = { group = "io.mockk", name = "mockk-android", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
truth = { group = "com.google.truth", name = "truth", version.ref = "truth" }
robolectric = { group = "org.robolectric", name = "robolectric", version.ref = "robolectric" }

# Firebase (Optional)
firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
firebase-crashlytics = { group = "com.google.firebase", name = "firebase-crashlytics-ktx" }
firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics-ktx" }

# Charts
vico-compose = { group = "com.patrykandpatrick.vico", name = "compose", version.ref = "vico" }
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }
vico-core = { group = "com.patrykandpatrick.vico", name = "core", version.ref = "vico" }

# Date/Time
kotlinx-datetime = { group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version.ref = "kotlinxDatetime" }

# Permissions
accompanist-permissions = { group = "com.google.accompanist", name = "accompanist-permissions", version.ref = "accompanistPermissions" }

# Image Loading (if needed)
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

### 3.2 Library Recommendations by Category

#### 3.2.1 Core Android (Required)
- ✅ **androidx.core:core-ktx**: Kotlin extensions
- ✅ **androidx.lifecycle**: Lifecycle-aware components
- ✅ **androidx.activity:activity-compose**: Compose activity support

#### 3.2.2 UI Framework (Required)
- ✅ **Jetpack Compose BOM**: Latest stable version
- ✅ **Material 3**: Modern design system
- ✅ **Navigation Compose**: Navigation between screens
- ✅ **Material Icons Extended**: Icon set

#### 3.2.3 Dependency Injection (Required)
- ✅ **Hilt**: Recommended DI framework
- ✅ **Hilt Navigation Compose**: Hilt integration với Navigation

#### 3.2.4 Database (Required)
- ✅ **Room**: Local database
- ✅ **Room KTX**: Coroutines support
- ✅ **Room Paging**: Pagination support (for future)

#### 3.2.5 Async & Concurrency (Required)
- ✅ **Kotlin Coroutines**: Async programming
- ✅ **Flow**: Reactive streams
- ✅ **WorkManager**: Background work

#### 3.2.6 Logging (Recommended)
- ✅ **Timber**: Better logging API

#### 3.2.7 Testing (Required)
- ✅ **JUnit**: Unit testing
- ✅ **MockK**: Mocking framework
- ✅ **Turbine**: Flow testing
- ✅ **Truth**: Assertions
- ✅ **Espresso**: UI testing
- ✅ **Robolectric**: Android unit testing

#### 3.2.8 Charts/Visualization (Recommended for V1.0)
- ✅ **Vico**: Modern chart library for Compose

#### 3.2.9 Date/Time (Recommended)
- ✅ **kotlinx-datetime**: Modern date/time API

#### 3.2.10 Permissions (Recommended)
- ✅ **Accompanist Permissions**: Easy permission handling

#### 3.2.11 Networking (Future - Phase 3)
- ✅ **Ktor**: HTTP client
- ✅ **kotlinx-serialization**: JSON serialization

#### 3.2.12 Firebase (Optional)
- ✅ **Firebase Crashlytics**: Crash reporting
- ✅ **Firebase Analytics**: Analytics (optional)

---

## 4. Architecture Patterns

### 4.1 MVVM Pattern

```kotlin
// ViewModel
@HiltViewModel
class SessionViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    private val endSessionUseCase: EndSessionUseCase,
    private val getActiveSessionUseCase: GetActiveSessionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()
    
    init {
        observeActiveSession()
    }
    
    fun startSession(goal: String) {
        viewModelScope.launch {
            startSessionUseCase(goal)
                .onSuccess { session ->
                    _uiState.update { it.copy(session = session, isTracking = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error) }
                }
        }
    }
    
    private fun observeActiveSession() {
        viewModelScope.launch {
            getActiveSessionUseCase()
                .collect { session ->
                    _uiState.update { it.copy(session = session) }
                }
        }
    }
}

// UI State
data class SessionUiState(
    val session: FocusSession? = null,
    val isTracking: Boolean = false,
    val elapsedTime: Duration = Duration.ZERO,
    val interruptions: List<Interruption> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = false
)
```

### 4.2 Repository Pattern

```kotlin
// Domain Interface
interface SessionRepository {
    suspend fun getActiveSession(): Flow<FocusSession?>
    suspend fun getSessionById(id: String): FocusSession?
    suspend fun getSessionsByDate(date: LocalDate): Flow<List<FocusSession>>
    suspend fun saveSession(session: FocusSession): Result<Unit>
    suspend fun updateSession(session: FocusSession): Result<Unit>
    suspend fun deleteSession(id: String): Result<Unit>
}

// Data Implementation
@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val interruptionDao: InterruptionDao,
    private val mapper: SessionMapper
) : SessionRepository {
    
    override suspend fun getActiveSession(): Flow<FocusSession?> {
        return sessionDao.getActiveSession()
            .map { entity -> entity?.let { mapper.toDomain(it) } }
    }
    
    override suspend fun saveSession(session: FocusSession): Result<Unit> {
        return try {
            val entity = mapper.toEntity(session)
            sessionDao.insertSession(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 4.3 Use Case Pattern

```kotlin
class StartSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val interruptionRepository: InterruptionRepository
) {
    suspend operator fun invoke(goal: String): Result<FocusSession> {
        return try {
            // Check for active session
            val activeSession = sessionRepository.getActiveSession().first()
            if (activeSession != null) {
                return Result.failure(DeepWorkError.ActiveSessionExists)
            }
            
            // Create new session
            val session = FocusSession(
                id = UUID.randomUUID().toString(),
                goal = goal,
                startTime = Instant.now(),
                endTime = null,
                totalDuration = Duration.ZERO,
                focusedDuration = Duration.ZERO
            )
            
            sessionRepository.saveSession(session)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(DeepWorkError.UnknownError(e.message ?: "Unknown error"))
        }
    }
}
```

### 4.4 Result Pattern

```kotlin
// Common Result wrapper
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onFailure(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}
```

---

## 5. Code Organization

### 5.1 Package Structure per Module

```
:feature:session/
├── presentation/
│   ├── SessionScreen.kt
│   ├── SessionViewModel.kt
│   └── SessionUiState.kt
├── domain/
│   ├── usecase/
│   │   ├── StartSessionUseCase.kt
│   │   ├── EndSessionUseCase.kt
│   │   ├── PauseSessionUseCase.kt
│   │   └── ResumeSessionUseCase.kt
│   └── repository/
│       └── SessionRepository.kt
└── data/
    ├── repository/
    │   └── SessionRepositoryImpl.kt
    └── local/
        └── SessionDao.kt
```

### 5.2 Naming Conventions

- **Screens**: `{Feature}Screen.kt` (e.g., `SessionScreen.kt`)
- **ViewModels**: `{Feature}ViewModel.kt` (e.g., `SessionViewModel.kt`)
- **UI States**: `{Feature}UiState.kt` (e.g., `SessionUiState.kt`)
- **Use Cases**: `{Action}{Entity}UseCase.kt` (e.g., `StartSessionUseCase.kt`)
- **Repositories**: `{Entity}Repository.kt` (interface), `{Entity}RepositoryImpl.kt` (implementation)
- **DAOs**: `{Entity}Dao.kt` (e.g., `SessionDao.kt`)
- **Entities**: `{Entity}Entity.kt` (e.g., `FocusSessionEntity.kt`)
- **Mappers**: `{Entity}Mapper.kt` (e.g., `SessionMapper.kt`)

---

## 6. Best Practices

### 6.1 Dependency Injection

```kotlin
// Module Definition
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeepWorkDatabase {
        return Room.databaseBuilder(
            context,
            DeepWorkDatabase::class.java,
            DeepWorkDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    fun provideSessionDao(database: DeepWorkDatabase): SessionDao {
        return database.sessionDao()
    }
}

// Use Case Module
@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    
    @Binds
    abstract fun bindStartSessionUseCase(
        useCase: StartSessionUseCase
    ): StartSessionUseCase
}
```

### 6.2 Error Handling

```kotlin
sealed class DeepWorkError : Throwable() {
    object SessionNotFound : DeepWorkError()
    object ActiveSessionExists : DeepWorkError()
    object NoActiveSession : DeepWorkError()
    object DatabaseError : DeepWorkError()
    object NetworkError : DeepWorkError()
    data class UnknownError(val message: String) : DeepWorkError()
}

// Usage in ViewModel
fun handleError(error: DeepWorkError) {
    val message = when (error) {
        is DeepWorkError.ActiveSessionExists -> "A session is already active"
        is DeepWorkError.SessionNotFound -> "Session not found"
        is DeepWorkError.UnknownError -> error.message
        else -> "An error occurred"
    }
    _uiState.update { it.copy(error = error, errorMessage = message) }
}
```

### 6.3 Testing Structure

```kotlin
// Unit Test
class StartSessionUseCaseTest {
    
    @Test
    fun `startSession creates new session when no active session exists`() = runTest {
        // Given
        val repository = mockk<SessionRepository>()
        val useCase = StartSessionUseCase(repository)
        
        coEvery { repository.getActiveSession() } returns flowOf(null)
        coEvery { repository.saveSession(any()) } returns Result.success(Unit)
        
        // When
        val result = useCase("Test goal")
        
        // Then
        assertTrue(result.isSuccess)
        verify { repository.saveSession(any()) }
    }
}
```

### 6.4 Logging

```kotlin
// Setup Timber in Application
class DeepWorkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree()) // Custom tree for production
        }
    }
}

// Usage
Timber.d("Session started: ${session.id}")
Timber.e(exception, "Failed to save session")
```

---

## 7. Build Configuration

### 7.1 Root build.gradle.kts

```kotlin
// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
```

### 7.2 Feature Module build.gradle.kts Template

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.deepworktracker.feature.session"
    compileSdk = 36
    
    defaultConfig {
        minSdk = 33
    }
    
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Domain
    implementation(project(":core:domain"))
    
    // Data
    implementation(project(":core:data"))
    
    // UI
    implementation(project(":core:ui"))
    
    // Common
    implementation(project(":core:common"))
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
```

---

## 8. Summary

### 8.1 Architecture Stack

- **Architecture**: Clean Architecture + MVVM
- **UI**: Jetpack Compose + Material 3
- **DI**: Hilt
- **Database**: Room
- **Async**: Coroutines + Flow
- **State Management**: StateFlow
- **Navigation**: Navigation Compose

### 8.2 Key Libraries

**Required**:
- Compose BOM, Material 3, Navigation Compose
- Hilt, Room, Coroutines, WorkManager

**Recommended**:
- Timber (logging), Vico (charts), kotlinx-datetime

**Future**:
- Ktor (networking), Firebase (analytics/crashlytics)

### 8.3 Module Strategy

- **Feature modules**: Feature-based, independent
- **Core modules**: Shared across features
- **Clear dependencies**: Feature → Core, no circular dependencies

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-23
