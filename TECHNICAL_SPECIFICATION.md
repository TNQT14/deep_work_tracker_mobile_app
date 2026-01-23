# Deep Work Tracker - Technical Specification

## 📋 Mục Lục
1. [Technical Stack](#1-technical-stack)
2. [Project Structure](#2-project-structure)
3. [Data Model & Database](#3-data-model--database)
4. [Interruption Tracking Implementation](#4-interruption-tracking-implementation)
5. [Insight Engine Rules](#5-insight-engine-rules)
6. [API Design (Future)](#6-api-design-future)
7. [Error Handling Strategy](#7-error-handling-strategy)
8. [Testing Strategy](#8-testing-strategy)

---

## 1. Technical Stack

### 1.1 Core Technologies

```kotlin
// Language
Kotlin 2.0.21

// Android
- Min SDK: 33 (Android 13)
- Target SDK: 36
- Compile SDK: 36

// UI Framework
Jetpack Compose (Material 3)

// Architecture
- Clean Architecture (3 layers: Presentation, Domain, Data)
- MVVM Pattern
- Repository Pattern

// Dependency Injection
Hilt (Dagger Hilt)

// Database
Room Database 2.6.1

// Async
Kotlin Coroutines + Flow

// Background Tasks
WorkManager (for insight generation, stats aggregation)

// Foreground Service
ForegroundService (for session tracking)
```

### 1.2 Dependencies (libs.versions.toml)

```toml
[versions]
# Core
agp = "8.13.2"
kotlin = "2.0.21"
coreKtx = "1.17.0"

# Compose
composeBom = "2024.09.00"
lifecycleRuntimeKtx = "2.10.0"
activityCompose = "1.12.2"

# Architecture Components
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

# Testing
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
mockk = "1.13.10"
turbine = "1.1.0"

[libraries]
# Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }

# Compose
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodel" }

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

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }

# WorkManager
androidx-work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workManager" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
```

---

## 2. Project Structure

### 2.1 Module Structure

```
deep_work_tracker/
├── app/                                    # Main application module
│   ├── src/main/
│   │   ├── java/com/deepworktracker/
│   │   │   ├── DeepWorkApplication.kt     # Application class
│   │   │   └── di/                        # Hilt modules
│   │   └── res/
│   └── build.gradle.kts
│
├── :feature:session/                      # Focus Session feature module
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
├── :feature:dashboard/                    # Dashboard feature module
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
├── :feature:insights/                     # Insights feature module
│   ├── src/main/java/com/deepworktracker/insights/
│   │   ├── presentation/
│   │   ├── domain/
│   │   │   └── usecase/
│   │   │       └── GenerateInsightUseCase.kt
│   │   └── data/
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
│   │       └── StatsRepository.kt
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
│   │   │   └── StatsRepositoryImpl.kt
│   │   └── mapper/
│   │       ├── SessionMapper.kt
│   │       └── StatsMapper.kt
│   └── build.gradle.kts
│
├── :core:common/                          # Common utilities
│   ├── src/main/java/com/deepworktracker/common/
│   │   ├── time/
│   │   │   └── TimeFormatter.kt
│   │   ├── extension/
│   │   │   └── DurationExtensions.kt
│   │   └── di/
│   │       └── DispatcherModule.kt
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
│   │       └── StatCard.kt
│   └── build.gradle.kts
│
└── build.gradle.kts                       # Root build file
```

### 2.2 Package Naming Convention

```
com.deepworktracker
├── feature
│   ├── session
│   ├── dashboard
│   ├── insights
│   └── settings
├── core
│   ├── domain
│   ├── data
│   ├── common
│   └── ui
└── app (root package)
```

---

## 3. Data Model & Database

### 3.1 Entity Definitions

#### 3.1.1 FocusSessionEntity

```kotlin
@Entity(
    tableName = "focus_sessions",
    indices = [
        Index(value = ["startTime"]),
        Index(value = ["date"])
    ]
)
data class FocusSessionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val goal: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Long, // Unix timestamp in milliseconds
    
    @ColumnInfo(name = "end_time")
    val endTime: Long?, // Null if session is active
    
    @ColumnInfo(name = "total_duration")
    val totalDuration: Long, // milliseconds
    
    @ColumnInfo(name = "focused_duration")
    val focusedDuration: Long, // milliseconds (total - interruptions)
    
    val tag: String?,
    val note: String?,
    
    // Computed field for querying by date
    @ColumnInfo(name = "date")
    val date: String, // YYYY-MM-DD format
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
```

#### 3.1.2 InterruptionEntity

```kotlin
@Entity(
    tableName = "interruptions",
    foreignKeys = [
        ForeignKey(
            entity = FocusSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["startTime"])
    ]
)
data class InterruptionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Long, // Unix timestamp in milliseconds
    
    @ColumnInfo(name = "end_time")
    val endTime: Long?, // Null if interruption is ongoing
    
    val type: String, // "BACKGROUND", "SCREEN_LOCK", "APP_SWITCH"
    
    val duration: Long // milliseconds
)
```

#### 3.1.3 DailyStatsEntity

```kotlin
@Entity(
    tableName = "daily_stats",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyStatsEntity(
    @PrimaryKey
    val date: String, // YYYY-MM-DD format
    
    @ColumnInfo(name = "total_focus_time")
    val totalFocusTime: Long, // milliseconds
    
    @ColumnInfo(name = "session_count")
    val sessionCount: Int,
    
    @ColumnInfo(name = "interruption_count")
    val interruptionCount: Int,
    
    @ColumnInfo(name = "average_session_duration")
    val averageSessionDuration: Long, // milliseconds
    
    @ColumnInfo(name = "best_focus_hour")
    val bestFocusHour: Int?, // 0-23, hour with most focus time
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
```

#### 3.1.4 InsightEntity

```kotlin
@Entity(
    tableName = "insights",
    indices = [Index(value = ["generatedAt"])]
)
data class InsightEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    val type: String, // "BEST_TIME_WINDOW", "OPTIMAL_SESSION_LENGTH", etc.
    
    val message: String,
    
    @ColumnInfo(name = "generated_at")
    val generatedAt: Long, // Unix timestamp
    
    val confidence: Float?, // 0.0 - 1.0, null for rule-based
    
    // Additional data as JSON string
    val data: String?, // JSON string for flexible data storage
    // Example: {"startHour": 8, "endHour": 10, "focusMinutes": 90}
    
    @ColumnInfo(name = "is_dismissed")
    val isDismissed: Boolean = false
)
```

### 3.2 DAO Definitions

#### 3.2.1 SessionDao

```kotlin
@Dao
interface SessionDao {
    
    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): FocusSessionEntity?
    
    @Query("SELECT * FROM focus_sessions WHERE end_time IS NULL LIMIT 1")
    suspend fun getActiveSession(): FocusSessionEntity?
    
    @Query("SELECT * FROM focus_sessions WHERE date = :date ORDER BY start_time DESC")
    fun getSessionsByDate(date: String): Flow<List<FocusSessionEntity>>
    
    @Query("SELECT * FROM focus_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY start_time DESC")
    fun getSessionsByDateRange(startDate: String, endDate: String): Flow<List<FocusSessionEntity>>
    
    @Query("SELECT * FROM focus_sessions ORDER BY start_time DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 50): Flow<List<FocusSessionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity)
    
    @Update
    suspend fun updateSession(session: FocusSessionEntity)
    
    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)
    
    @Query("DELETE FROM focus_sessions WHERE date < :beforeDate")
    suspend fun deleteSessionsBefore(beforeDate: String)
}
```

#### 3.2.2 InterruptionDao

```kotlin
@Dao
interface InterruptionDao {
    
    @Query("SELECT * FROM interruptions WHERE session_id = :sessionId ORDER BY start_time ASC")
    fun getInterruptionsBySession(sessionId: String): Flow<List<InterruptionEntity>>
    
    @Query("SELECT * FROM interruptions WHERE session_id = :sessionId AND end_time IS NULL LIMIT 1")
    suspend fun getActiveInterruption(sessionId: String): InterruptionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterruption(interruption: InterruptionEntity)
    
    @Update
    suspend fun updateInterruption(interruption: InterruptionEntity)
    
    @Query("DELETE FROM interruptions WHERE session_id = :sessionId")
    suspend fun deleteInterruptionsBySession(sessionId: String)
}
```

#### 3.2.3 StatsDao

```kotlin
@Dao
interface StatsDao {
    
    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getDailyStats(date: String): DailyStatsEntity?
    
    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getStatsByDateRange(startDate: String, endDate: String): Flow<List<DailyStatsEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: DailyStatsEntity)
    
    @Query("SELECT SUM(total_focus_time) FROM daily_stats WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalFocusTimeInRange(startDate: String, endDate: String): Long?
}
```

#### 3.2.4 InsightDao

```kotlin
@Dao
interface InsightDao {
    
    @Query("SELECT * FROM insights WHERE is_dismissed = 0 ORDER BY generated_at DESC LIMIT :limit")
    fun getRecentInsights(limit: Int = 10): Flow<List<InsightEntity>>
    
    @Query("SELECT * FROM insights WHERE type = :type AND is_dismissed = 0 ORDER BY generated_at DESC LIMIT 1")
    suspend fun getLatestInsightByType(type: String): InsightEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: InsightEntity)
    
    @Query("UPDATE insights SET is_dismissed = 1 WHERE id = :id")
    suspend fun dismissInsight(id: String)
    
    @Query("DELETE FROM insights WHERE generated_at < :beforeTimestamp")
    suspend fun deleteOldInsights(beforeTimestamp: Long)
}
```

### 3.3 Database Configuration

```kotlin
@Database(
    entities = [
        FocusSessionEntity::class,
        InterruptionEntity::class,
        DailyStatsEntity::class,
        InsightEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DeepWorkDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun interruptionDao(): InterruptionDao
    abstract fun statsDao(): StatsDao
    abstract fun insightDao(): InsightDao
    
    companion object {
        const val DATABASE_NAME = "deep_work_db"
    }
}

// Type converters for Room
class Converters {
    // Add converters if needed for complex types
}
```

---

## 4. Interruption Tracking Implementation

### 4.1 Detection Methods

#### 4.1.1 App Lifecycle Tracking

```kotlin
class AppLifecycleTracker(
    private val interruptionRepository: InterruptionRepository
) : Application.ActivityLifecycleCallbacks {
    
    private var isAppInForeground = true
    private var currentSessionId: String? = null
    
    override fun onActivityResumed(activity: Activity) {
        if (!isAppInForeground && currentSessionId != null) {
            // App came back to foreground, end interruption
            interruptionRepository.endInterruption(currentSessionId!!)
        }
        isAppInForeground = true
    }
    
    override fun onActivityPaused(activity: Activity) {
        isAppInForeground = false
        if (currentSessionId != null) {
            // App went to background, start interruption
            interruptionRepository.startInterruption(
                sessionId = currentSessionId!!,
                type = InterruptionType.BACKGROUND
            )
        }
    }
    
    // Other lifecycle callbacks...
}
```

#### 4.1.2 Screen Lock Detection

```kotlin
class ScreenLockTracker(
    private val context: Context,
    private val interruptionRepository: InterruptionRepository
) {
    
    private val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    
    fun checkScreenLock(): Boolean {
        return keyguardManager.isKeyguardLocked
    }
    
    fun startTracking(sessionId: String) {
        // Use BroadcastReceiver to detect screen on/off
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        interruptionRepository.startInterruption(
                            sessionId = sessionId,
                            type = InterruptionType.SCREEN_LOCK
                        )
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        interruptionRepository.endInterruption(sessionId)
                    }
                }
            }
        }
        context.registerReceiver(receiver, filter)
    }
}
```

#### 4.1.3 App Switch Detection (UsageStatsManager)

```kotlin
class AppSwitchTracker(
    private val context: Context,
    private val interruptionRepository: InterruptionRepository
) {
    
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    
    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    suspend fun detectAppSwitch(sessionId: String): Boolean {
        if (!hasUsageStatsPermission()) return false
        
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            time - 5000, // Last 5 seconds
            time
        )
        
        val currentApp = stats.maxByOrNull { it.lastTimeUsed }?.packageName
        val ourPackage = context.packageName
        
        return currentApp != null && currentApp != ourPackage
    }
}
```

### 4.2 Interruption Service

```kotlin
class InterruptionTrackingService : Service() {
    
    private val binder = LocalBinder()
    private var currentSessionId: String? = null
    private var trackingJob: Job? = null
    
    inner class LocalBinder : Binder() {
        fun getService(): InterruptionTrackingService = this@InterruptionTrackingService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    fun startTracking(sessionId: String) {
        currentSessionId = sessionId
        trackingJob = CoroutineScope(Dispatchers.Default).launch {
            while (currentSessionId != null) {
                checkInterruptions()
                delay(2000) // Check every 2 seconds
            }
        }
    }
    
    fun stopTracking() {
        currentSessionId = null
        trackingJob?.cancel()
    }
    
    private suspend fun checkInterruptions() {
        val sessionId = currentSessionId ?: return
        
        // Check screen lock
        if (screenLockTracker.checkScreenLock()) {
            interruptionRepository.startInterruption(sessionId, InterruptionType.SCREEN_LOCK)
        }
        
        // Check app switch
        if (appSwitchTracker.detectAppSwitch(sessionId)) {
            interruptionRepository.startInterruption(sessionId, InterruptionType.APP_SWITCH)
        }
    }
}
```

---

## 5. Insight Engine Rules

### 5.1 Rule Definitions

#### 5.1.1 Best Time Window Insight

```kotlin
class BestTimeWindowRule(
    private val sessionRepository: SessionRepository
) {
    
    suspend fun generateInsight(): Insight? {
        val sessions = sessionRepository.getSessionsLast30Days()
        if (sessions.size < 10) return null // Need minimum data
        
        // Group sessions by hour
        val focusByHour = mutableMapOf<Int, Long>()
        
        sessions.forEach { session ->
            val hour = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(session.startTime),
                ZoneId.systemDefault()
            ).hour
            
            focusByHour[hour] = (focusByHour[hour] ?: 0) + session.focusedDuration.toMinutes()
        }
        
        // Find hour with most focus
        val bestHour = focusByHour.maxByOrNull { it.value }?.key
        if (bestHour == null) return null
        
        // Find time window (best hour ± 1 hour)
        val startHour = (bestHour - 1).coerceAtLeast(0)
        val endHour = (bestHour + 1).coerceAtMost(23)
        
        val message = "Bạn tập trung tốt nhất từ ${startHour}h đến ${endHour}h."
        
        return Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.BEST_TIME_WINDOW,
            message = message,
            generatedAt = Instant.now(),
            confidence = 0.8f,
            data = mapOf(
                "startHour" to startHour,
                "endHour" to endHour,
                "focusMinutes" to focusByHour[bestHour]
            )
        )
    }
}
```

#### 5.1.2 Optimal Session Length Insight

```kotlin
class OptimalSessionLengthRule(
    private val sessionRepository: SessionRepository
) {
    
    suspend fun generateInsight(): Insight? {
        val sessions = sessionRepository.getSessionsLast30Days()
        if (sessions.size < 20) return null
        
        // Calculate average session length before first interruption
        val sessionsWithInterruptions = sessions.filter { it.interruptions.isNotEmpty() }
        if (sessionsWithInterruptions.isEmpty()) return null
        
        val timeToFirstInterruption = sessionsWithInterruptions.map { session ->
            val firstInterruption = session.interruptions.minByOrNull { it.startTime }
            if (firstInterruption != null) {
                Duration.between(
                    Instant.ofEpochMilli(session.startTime),
                    Instant.ofEpochMilli(firstInterruption.startTime)
                ).toMinutes()
            } else null
        }.filterNotNull()
        
        if (timeToFirstInterruption.isEmpty()) return null
        
        val averageMinutes = timeToFirstInterruption.average().toInt()
        
        val message = "Sau $averageMinutes phút, khả năng gián đoạn tăng đáng kể."
        
        return Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.OPTIMAL_SESSION_LENGTH,
            message = message,
            generatedAt = Instant.now(),
            confidence = 0.75f,
            data = mapOf(
                "optimalMinutes" to averageMinutes,
                "sampleSize" to timeToFirstInterruption.size
            )
        )
    }
}
```

#### 5.1.3 Distraction Pattern Insight

```kotlin
class DistractionPatternRule(
    private val sessionRepository: SessionRepository
) {
    
    suspend fun generateInsight(): Insight? {
        val sessions = sessionRepository.getSessionsLast30Days()
        if (sessions.size < 14) return null // Need at least 2 weeks
        
        // Group by day of week
        val interruptionsByDay = mutableMapOf<DayOfWeek, Int>()
        
        sessions.forEach { session ->
            val dayOfWeek = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(session.startTime),
                ZoneId.systemDefault()
            ).dayOfWeek
            
            interruptionsByDay[dayOfWeek] = 
                (interruptionsByDay[dayOfWeek] ?: 0) + session.interruptions.size
        }
        
        val worstDay = interruptionsByDay.maxByOrNull { it.value }?.key
        if (worstDay == null) return null
        
        val dayName = when (worstDay) {
            DayOfWeek.MONDAY -> "Thứ Hai"
            DayOfWeek.TUESDAY -> "Thứ Ba"
            // ... other days
            else -> worstDay.name
        }
        
        val message = "Bạn thường bị phân tâm nhiều nhất vào $dayName."
        
        return Insight(
            id = UUID.randomUUID().toString(),
            type = InsightType.DISTRACTION_PATTERN,
            message = message,
            generatedAt = Instant.now(),
            confidence = 0.7f,
            data = mapOf(
                "dayOfWeek" to worstDay.value,
                "interruptionCount" to interruptionsByDay[worstDay]
            )
        )
    }
}
```

### 5.2 Insight Generation Service

```kotlin
class InsightGenerationService(
    private val rules: List<InsightRule>,
    private val insightRepository: InsightRepository
) {
    
    suspend fun generateInsights(): List<Insight> {
        return rules.mapNotNull { rule ->
            try {
                rule.generateInsight()
            } catch (e: Exception) {
                // Log error, don't fail entire generation
                null
            }
        }
    }
    
    suspend fun generateAndSaveInsights() {
        val insights = generateInsights()
        insights.forEach { insight ->
            insightRepository.saveInsight(insight)
        }
    }
}
```

---

## 6. API Design (Future)

### 6.1 Authentication

```kotlin
// Request
POST /api/auth/register
{
  "email": "user@example.com",
  "password": "securePassword",
  "name": "User Name"
}

// Response
{
  "success": true,
  "data": {
    "token": "jwt_token_here",
    "user": {
      "id": "user_id",
      "email": "user@example.com",
      "name": "User Name"
    }
  }
}
```

### 6.2 Session Sync

```kotlin
// Push local sessions
POST /api/sync/push
Headers: Authorization: Bearer {token}
Body: {
  "sessions": [
    {
      "id": "local_session_id",
      "goal": "Code feature",
      "startTime": 1706000000000,
      "endTime": 1706003600000,
      "totalDuration": 3600000,
      "focusedDuration": 3300000,
      "tag": "coding",
      "note": "Completed",
      "interruptions": [...]
    }
  ],
  "lastSyncTimestamp": 1706000000000
}

// Response
{
  "success": true,
  "data": {
    "syncedCount": 5,
    "conflicts": []
  }
}
```

---

## 7. Error Handling Strategy

### 7.1 Error Types

```kotlin
sealed class DeepWorkError {
    object SessionNotFound : DeepWorkError()
    object ActiveSessionExists : DeepWorkError()
    object NoActiveSession : DeepWorkError()
    object DatabaseError : DeepWorkError()
    object NetworkError : DeepWorkError()
    data class UnknownError(val message: String) : DeepWorkError()
}
```

### 7.2 Error Handling in Use Cases

```kotlin
class StartSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(goal: String): Result<FocusSession> {
        return try {
            // Check for active session
            val activeSession = sessionRepository.getActiveSession()
            if (activeSession != null) {
                return Result.failure(DeepWorkError.ActiveSessionExists)
            }
            
            // Create new session
            val session = FocusSession(
                id = UUID.randomUUID().toString(),
                goal = goal,
                startTime = Instant.now(),
                // ... other fields
            )
            
            sessionRepository.saveSession(session)
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(DeepWorkError.UnknownError(e.message ?: "Unknown error"))
        }
    }
}
```

---

## 8. Testing Strategy

### 8.1 Unit Tests

```kotlin
class StartSessionUseCaseTest {
    
    @Test
    fun `startSession creates new session when no active session exists`() = runTest {
        // Given
        val repository = mockk<SessionRepository>()
        val useCase = StartSessionUseCase(repository)
        
        coEvery { repository.getActiveSession() } returns null
        coEvery { repository.saveSession(any()) } just Runs
        
        // When
        val result = useCase("Test goal")
        
        // Then
        assertTrue(result.isSuccess)
        verify { repository.saveSession(any()) }
    }
}
```

### 8.2 Integration Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class SessionRepositoryTest {
    
    private lateinit var database: DeepWorkDatabase
    private lateinit var repository: SessionRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DeepWorkDatabase::class.java
        ).build()
        repository = SessionRepositoryImpl(database.sessionDao())
    }
    
    @Test
    fun saveAndRetrieveSession() = runTest {
        // Test implementation
    }
}
```

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-23
