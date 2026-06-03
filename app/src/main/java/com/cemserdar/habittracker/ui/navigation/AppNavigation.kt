package com.cemserdar.habittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cemserdar.habittracker.ui.screens.AddHabitScreen
import com.cemserdar.habittracker.ui.screens.HabitDetailScreen
import com.cemserdar.habittracker.ui.screens.HomeScreen
import com.cemserdar.habittracker.viewmodel.HabitViewModel

@Composable
fun AppNavigation(
    viewModel: HabitViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToAddHabit = { navController.navigate("add_habit") },
                onHabitClick = { habitId -> navController.navigate("habit_detail/$habitId") }
            )
        }
        composable("add_habit") {
            AddHabitScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "habit_detail/{habitId}",
            arguments = listOf(navArgument("habitId") { type = NavType.IntType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getInt("habitId") ?: return@composable
            HabitDetailScreen(
                habitId = habitId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
