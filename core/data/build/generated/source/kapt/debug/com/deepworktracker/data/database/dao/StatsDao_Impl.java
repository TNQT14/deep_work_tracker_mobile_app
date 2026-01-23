package com.deepworktracker.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.deepworktracker.data.database.entity.DailyStatsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StatsDao_Impl implements StatsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyStatsEntity> __insertionAdapterOfDailyStatsEntity;

  public StatsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyStatsEntity = new EntityInsertionAdapter<DailyStatsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_stats` (`date`,`total_focus_time`,`session_count`,`interruption_count`,`average_session_duration`,`best_focus_hour`,`updated_at`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyStatsEntity entity) {
        if (entity.getDate() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getDate());
        }
        statement.bindLong(2, entity.getTotalFocusTime());
        statement.bindLong(3, entity.getSessionCount());
        statement.bindLong(4, entity.getInterruptionCount());
        statement.bindLong(5, entity.getAverageSessionDuration());
        if (entity.getBestFocusHour() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getBestFocusHour());
        }
        statement.bindLong(7, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public Object insertStats(final DailyStatsEntity stats,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyStatsEntity.insert(stats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDailyStats(final String date,
      final Continuation<? super DailyStatsEntity> $completion) {
    final String _sql = "SELECT * FROM daily_stats WHERE date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyStatsEntity>() {
      @Override
      @Nullable
      public DailyStatsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTotalFocusTime = CursorUtil.getColumnIndexOrThrow(_cursor, "total_focus_time");
          final int _cursorIndexOfSessionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "session_count");
          final int _cursorIndexOfInterruptionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "interruption_count");
          final int _cursorIndexOfAverageSessionDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "average_session_duration");
          final int _cursorIndexOfBestFocusHour = CursorUtil.getColumnIndexOrThrow(_cursor, "best_focus_hour");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final DailyStatsEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final long _tmpTotalFocusTime;
            _tmpTotalFocusTime = _cursor.getLong(_cursorIndexOfTotalFocusTime);
            final int _tmpSessionCount;
            _tmpSessionCount = _cursor.getInt(_cursorIndexOfSessionCount);
            final int _tmpInterruptionCount;
            _tmpInterruptionCount = _cursor.getInt(_cursorIndexOfInterruptionCount);
            final long _tmpAverageSessionDuration;
            _tmpAverageSessionDuration = _cursor.getLong(_cursorIndexOfAverageSessionDuration);
            final Integer _tmpBestFocusHour;
            if (_cursor.isNull(_cursorIndexOfBestFocusHour)) {
              _tmpBestFocusHour = null;
            } else {
              _tmpBestFocusHour = _cursor.getInt(_cursorIndexOfBestFocusHour);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyStatsEntity(_tmpDate,_tmpTotalFocusTime,_tmpSessionCount,_tmpInterruptionCount,_tmpAverageSessionDuration,_tmpBestFocusHour,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyStatsEntity>> getStatsByDateRange(final String startDate,
      final String endDate) {
    final String _sql = "SELECT * FROM daily_stats WHERE date BETWEEN ? AND ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (startDate == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, startDate);
    }
    _argIndex = 2;
    if (endDate == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, endDate);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_stats"}, new Callable<List<DailyStatsEntity>>() {
      @Override
      @NonNull
      public List<DailyStatsEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTotalFocusTime = CursorUtil.getColumnIndexOrThrow(_cursor, "total_focus_time");
          final int _cursorIndexOfSessionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "session_count");
          final int _cursorIndexOfInterruptionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "interruption_count");
          final int _cursorIndexOfAverageSessionDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "average_session_duration");
          final int _cursorIndexOfBestFocusHour = CursorUtil.getColumnIndexOrThrow(_cursor, "best_focus_hour");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<DailyStatsEntity> _result = new ArrayList<DailyStatsEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyStatsEntity _item;
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final long _tmpTotalFocusTime;
            _tmpTotalFocusTime = _cursor.getLong(_cursorIndexOfTotalFocusTime);
            final int _tmpSessionCount;
            _tmpSessionCount = _cursor.getInt(_cursorIndexOfSessionCount);
            final int _tmpInterruptionCount;
            _tmpInterruptionCount = _cursor.getInt(_cursorIndexOfInterruptionCount);
            final long _tmpAverageSessionDuration;
            _tmpAverageSessionDuration = _cursor.getLong(_cursorIndexOfAverageSessionDuration);
            final Integer _tmpBestFocusHour;
            if (_cursor.isNull(_cursorIndexOfBestFocusHour)) {
              _tmpBestFocusHour = null;
            } else {
              _tmpBestFocusHour = _cursor.getInt(_cursorIndexOfBestFocusHour);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyStatsEntity(_tmpDate,_tmpTotalFocusTime,_tmpSessionCount,_tmpInterruptionCount,_tmpAverageSessionDuration,_tmpBestFocusHour,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTotalFocusTimeInRange(final String startDate, final String endDate,
      final Continuation<? super Long> $completion) {
    final String _sql = "SELECT SUM(total_focus_time) FROM daily_stats WHERE date BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (startDate == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, startDate);
    }
    _argIndex = 2;
    if (endDate == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, endDate);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
