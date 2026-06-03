package com.cemserdar.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cemserdar.habittracker.data.Habit
import com.cemserdar.habittracker.data.HabitLog
import com.cemserdar.habittracker.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HabitWithStreak(
    val habit: Habit,
    val currentStreak: Int,
    val bestStreak: Int
)

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    val todayStr: String = LocalDate.now().format(dateFormatter)

    val habitsWithStreak: StateFlow<List<HabitWithStreak>> = combine(
        repository.allHabits,
        repository.allLogs
    ) { habitList, logsList ->
        habitList.map { habit ->
            val habitLogs = logsList.filter { it.habitId == habit.id }
                .map { LocalDate.parse(it.dateCompleted, dateFormatter) }
                .sortedDescending()
                
            val currentStreak = calculateCurrentStreak(habitLogs)
            val bestStreak = calculateBestStreak(habitLogs)
            
            HabitWithStreak(habit, currentStreak, bestStreak)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayLogs: StateFlow<List<HabitLog>> = repository.getLogsForDate(todayStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addHabit(name: String, description: String, color: Long) {
        viewModelScope.launch {
            repository.insertHabit(Habit(name = name, description = description, color = color))
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleHabitCompletion(habitId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, todayStr, isCompleted)
        }
    }
    
    private fun calculateCurrentStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        
        var streak = 0
        var expectedDate = LocalDate.now()
        
        // If not completed today, maybe it was completed yesterday. Check yesterday.
        if (!dates.contains(expectedDate)) {
            expectedDate = expectedDate.minusDays(1)
            if (!dates.contains(expectedDate)) return 0 // Streak broken
        }
        
        for (date in dates) {
            if (date == expectedDate) {
                streak++
                expectedDate = expectedDate.minusDays(1)
            } else if (date.isBefore(expectedDate)) {
                break
            }
        }
        return streak
    }
    
    private fun calculateBestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        var bestStreak = 0
        var currentStreak = 1
        var previousDate = dates.last() // oldest date
        
        val sortedDates = dates.sorted() // oldest to newest
        
        for (i in 1 until sortedDates.size) {
            val date = sortedDates[i]
            if (date == previousDate.plusDays(1)) {
                currentStreak++
            } else if (date != previousDate) { // Reset if not the same day
                if (currentStreak > bestStreak) bestStreak = currentStreak
                currentStreak = 1
            }
            previousDate = date
        }
        if (currentStreak > bestStreak) bestStreak = currentStreak
        return bestStreak
    }
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
