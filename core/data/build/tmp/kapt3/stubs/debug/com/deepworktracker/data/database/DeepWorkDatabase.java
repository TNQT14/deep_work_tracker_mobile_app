package com.deepworktracker.data.database;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&\u00a8\u0006\f"}, d2 = {"Lcom/deepworktracker/data/database/DeepWorkDatabase;", "Landroidx/room/RoomDatabase;", "()V", "insightDao", "Lcom/deepworktracker/data/database/dao/InsightDao;", "interruptionDao", "Lcom/deepworktracker/data/database/dao/InterruptionDao;", "sessionDao", "Lcom/deepworktracker/data/database/dao/SessionDao;", "statsDao", "Lcom/deepworktracker/data/database/dao/StatsDao;", "Companion", "data_debug"})
@androidx.room.Database(entities = {com.deepworktracker.data.database.entity.FocusSessionEntity.class, com.deepworktracker.data.database.entity.InterruptionEntity.class, com.deepworktracker.data.database.entity.DailyStatsEntity.class, com.deepworktracker.data.database.entity.InsightEntity.class}, version = 1, exportSchema = false)
public abstract class DeepWorkDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DATABASE_NAME = "deep_work_db";
    @org.jetbrains.annotations.NotNull()
    public static final com.deepworktracker.data.database.DeepWorkDatabase.Companion Companion = null;
    
    public DeepWorkDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.deepworktracker.data.database.dao.SessionDao sessionDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.deepworktracker.data.database.dao.InterruptionDao interruptionDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.deepworktracker.data.database.dao.StatsDao statsDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.deepworktracker.data.database.dao.InsightDao insightDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/deepworktracker/data/database/DeepWorkDatabase$Companion;", "", "()V", "DATABASE_NAME", "", "data_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}