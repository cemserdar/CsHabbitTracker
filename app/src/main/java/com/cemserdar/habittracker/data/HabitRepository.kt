package com.cemserdar.habittracker.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    
    val allLogs: Flow<List<HabitLog>> = habitDao.getAllLogs()

    fun getLogsForDate(date: String): Flow<List<HabitLog>> {
        return habitDao.getLogsForDate(date)
    }

    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }

    suspend fun toggleHabitCompletion(habitId: Int, date: String, isCompleted: Boolean) {
        if (isCompleted) {
            habitDao.insertHabitLog(HabitLog(habitId = habitId, dateCompleted = date))
        } else {
            habitDao.deleteHabitLog(habitId = habitId, dateCompleted = date)
        }
    }
}
