package com.cemserdar.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabitLog(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateCompleted = :dateCompleted")
    suspend fun deleteHabitLog(habitId: Int, dateCompleted: String)

    @Query("SELECT * FROM habit_logs WHERE dateCompleted = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLog>>
    
    @Query("SELECT * FROM habit_logs")
    fun getAllLogs(): Flow<List<HabitLog>>
}
