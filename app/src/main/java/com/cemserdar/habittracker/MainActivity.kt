package com.cemserdar.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cemserdar.habittracker.data.AppDatabase
import com.cemserdar.habittracker.data.HabitRepository
import com.cemserdar.habittracker.ui.navigation.AppNavigation
import com.cemserdar.habittracker.ui.theme.HabitTrackerTheme
import com.cemserdar.habittracker.viewmodel.HabitViewModel
import com.cemserdar.habittracker.viewmodel.HabitViewModelFactory

class MainActivity : ComponentActivity() {
    
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { HabitRepository(database.habitDao()) }
    
    private val viewModel: HabitViewModel by viewModels {
        HabitViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            HabitTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
