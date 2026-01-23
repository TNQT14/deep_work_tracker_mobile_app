package com.deepworktracker.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.deepworktracker.data.database.dao.InsightDao;
import com.deepworktracker.data.database.dao.InsightDao_Impl;
import com.deepworktracker.data.database.dao.InterruptionDao;
import com.deepworktracker.data.database.dao.InterruptionDao_Impl;
import com.deepworktracker.data.database.dao.SessionDao;
import com.deepworktracker.data.database.dao.SessionDao_Impl;
import com.deepworktracker.data.database.dao.StatsDao;
import com.deepworktracker.data.database.dao.StatsDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DeepWorkDatabase_Impl extends DeepWorkDatabase {
  private volatile SessionDao _sessionDao;

  private volatile InterruptionDao _interruptionDao;

  private volatile StatsDao _statsDao;

  private volatile InsightDao _insightDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `focus_sessions` (`id` TEXT NOT NULL, `goal` TEXT NOT NULL, `start_time` INTEGER NOT NULL, `end_time` INTEGER, `total_duration` INTEGER NOT NULL, `focused_duration` INTEGER NOT NULL, `tag` TEXT, `note` TEXT, `date` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_focus_sessions_start_time` ON `focus_sessions` (`start_time`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_focus_sessions_date` ON `focus_sessions` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `interruptions` (`id` TEXT NOT NULL, `session_id` TEXT NOT NULL, `start_time` INTEGER NOT NULL, `end_time` INTEGER, `type` TEXT NOT NULL, `duration` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`session_id`) REFERENCES `focus_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_interruptions_session_id` ON `interruptions` (`session_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_interruptions_start_time` ON `interruptions` (`start_time`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_stats` (`date` TEXT NOT NULL, `total_focus_time` INTEGER NOT NULL, `session_count` INTEGER NOT NULL, `interruption_count` INTEGER NOT NULL, `average_session_duration` INTEGER NOT NULL, `best_focus_hour` INTEGER, `updated_at` INTEGER NOT NULL, PRIMARY KEY(`date`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_stats_date` ON `daily_stats` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `insights` (`id` TEXT NOT NULL, `type` TEXT NOT NULL, `message` TEXT NOT NULL, `generated_at` INTEGER NOT NULL, `confidence` REAL, `data` TEXT, `is_dismissed` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_insights_generated_at` ON `insights` (`generated_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'db1ff06ea35be2462b71149d13e99e0e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `focus_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `interruptions`");
        db.execSQL("DROP TABLE IF EXISTS `daily_stats`");
        db.execSQL("DROP TABLE IF EXISTS `insights`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFocusSessions = new HashMap<String, TableInfo.Column>(11);
        _columnsFocusSessions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("goal", new TableInfo.Column("goal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("start_time", new TableInfo.Column("start_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("end_time", new TableInfo.Column("end_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("total_duration", new TableInfo.Column("total_duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("focused_duration", new TableInfo.Column("focused_duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("tag", new TableInfo.Column("tag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFocusSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFocusSessions = new HashSet<TableInfo.Index>(2);
        _indicesFocusSessions.add(new TableInfo.Index("index_focus_sessions_start_time", false, Arrays.asList("start_time"), Arrays.asList("ASC")));
        _indicesFocusSessions.add(new TableInfo.Index("index_focus_sessions_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoFocusSessions = new TableInfo("focus_sessions", _columnsFocusSessions, _foreignKeysFocusSessions, _indicesFocusSessions);
        final TableInfo _existingFocusSessions = TableInfo.read(db, "focus_sessions");
        if (!_infoFocusSessions.equals(_existingFocusSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "focus_sessions(com.deepworktracker.data.database.entity.FocusSessionEntity).\n"
                  + " Expected:\n" + _infoFocusSessions + "\n"
                  + " Found:\n" + _existingFocusSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsInterruptions = new HashMap<String, TableInfo.Column>(6);
        _columnsInterruptions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInterruptions.put("session_id", new TableInfo.Column("session_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInterruptions.put("start_time", new TableInfo.Column("start_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInterruptions.put("end_time", new TableInfo.Column("end_time", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInterruptions.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInterruptions.put("duration", new TableInfo.Column("duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInterruptions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysInterruptions.add(new TableInfo.ForeignKey("focus_sessions", "CASCADE", "NO ACTION", Arrays.asList("session_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesInterruptions = new HashSet<TableInfo.Index>(2);
        _indicesInterruptions.add(new TableInfo.Index("index_interruptions_session_id", false, Arrays.asList("session_id"), Arrays.asList("ASC")));
        _indicesInterruptions.add(new TableInfo.Index("index_interruptions_start_time", false, Arrays.asList("start_time"), Arrays.asList("ASC")));
        final TableInfo _infoInterruptions = new TableInfo("interruptions", _columnsInterruptions, _foreignKeysInterruptions, _indicesInterruptions);
        final TableInfo _existingInterruptions = TableInfo.read(db, "interruptions");
        if (!_infoInterruptions.equals(_existingInterruptions)) {
          return new RoomOpenHelper.ValidationResult(false, "interruptions(com.deepworktracker.data.database.entity.InterruptionEntity).\n"
                  + " Expected:\n" + _infoInterruptions + "\n"
                  + " Found:\n" + _existingInterruptions);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyStats = new HashMap<String, TableInfo.Column>(7);
        _columnsDailyStats.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("total_focus_time", new TableInfo.Column("total_focus_time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("session_count", new TableInfo.Column("session_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("interruption_count", new TableInfo.Column("interruption_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("average_session_duration", new TableInfo.Column("average_session_duration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("best_focus_hour", new TableInfo.Column("best_focus_hour", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyStats.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyStats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyStats = new HashSet<TableInfo.Index>(1);
        _indicesDailyStats.add(new TableInfo.Index("index_daily_stats_date", true, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoDailyStats = new TableInfo("daily_stats", _columnsDailyStats, _foreignKeysDailyStats, _indicesDailyStats);
        final TableInfo _existingDailyStats = TableInfo.read(db, "daily_stats");
        if (!_infoDailyStats.equals(_existingDailyStats)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_stats(com.deepworktracker.data.database.entity.DailyStatsEntity).\n"
                  + " Expected:\n" + _infoDailyStats + "\n"
                  + " Found:\n" + _existingDailyStats);
        }
        final HashMap<String, TableInfo.Column> _columnsInsights = new HashMap<String, TableInfo.Column>(7);
        _columnsInsights.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInsights.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInsights.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInsights.put("generated_at", new TableInfo.Column("generated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInsights.put("confidence", new TableInfo.Column("confidence", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInsights.put("data", new TableInfo.Column("data", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInsights.put("is_dismissed", new TableInfo.Column("is_dismissed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInsights = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesInsights = new HashSet<TableInfo.Index>(1);
        _indicesInsights.add(new TableInfo.Index("index_insights_generated_at", false, Arrays.asList("generated_at"), Arrays.asList("ASC")));
        final TableInfo _infoInsights = new TableInfo("insights", _columnsInsights, _foreignKeysInsights, _indicesInsights);
        final TableInfo _existingInsights = TableInfo.read(db, "insights");
        if (!_infoInsights.equals(_existingInsights)) {
          return new RoomOpenHelper.ValidationResult(false, "insights(com.deepworktracker.data.database.entity.InsightEntity).\n"
                  + " Expected:\n" + _infoInsights + "\n"
                  + " Found:\n" + _existingInsights);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "db1ff06ea35be2462b71149d13e99e0e", "a01ff9f064730a731c3e4dbce22c0df7");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "focus_sessions","interruptions","daily_stats","insights");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `focus_sessions`");
      _db.execSQL("DELETE FROM `interruptions`");
      _db.execSQL("DELETE FROM `daily_stats`");
      _db.execSQL("DELETE FROM `insights`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(SessionDao.class, SessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InterruptionDao.class, InterruptionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StatsDao.class, StatsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(InsightDao.class, InsightDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public SessionDao sessionDao() {
    if (_sessionDao != null) {
      return _sessionDao;
    } else {
      synchronized(this) {
        if(_sessionDao == null) {
          _sessionDao = new SessionDao_Impl(this);
        }
        return _sessionDao;
      }
    }
  }

  @Override
  public InterruptionDao interruptionDao() {
    if (_interruptionDao != null) {
      return _interruptionDao;
    } else {
      synchronized(this) {
        if(_interruptionDao == null) {
          _interruptionDao = new InterruptionDao_Impl(this);
        }
        return _interruptionDao;
      }
    }
  }

  @Override
  public StatsDao statsDao() {
    if (_statsDao != null) {
      return _statsDao;
    } else {
      synchronized(this) {
        if(_statsDao == null) {
          _statsDao = new StatsDao_Impl(this);
        }
        return _statsDao;
      }
    }
  }

  @Override
  public InsightDao insightDao() {
    if (_insightDao != null) {
      return _insightDao;
    } else {
      synchronized(this) {
        if(_insightDao == null) {
          _insightDao = new InsightDao_Impl(this);
        }
        return _insightDao;
      }
    }
  }
}
