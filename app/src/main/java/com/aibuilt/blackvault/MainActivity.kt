package com.aibuilt.blackvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aibuilt.blackvault.ui.BreachScreen
import com.aibuilt.blackvault.ui.DashboardScreen
import com.aibuilt.blackvault.ui.HistoryScreen
import com.aibuilt.blackvault.ui.ReceiptScreen
import com.aibuilt.blackvault.ui.SettingsScreen
import com.aibuilt.blackvault.ui.SplashScreen
import com.aibuilt.blackvault.ui.theme.BlackVaultTheme
import com.aibuilt.blackvault.ui.theme.DimText
import com.aibuilt.blackvault.viewmodel.VaultViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            BlackVaultTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                    VaultApp()
                }
            }
        }
    }
}

@Composable
fun VaultApp() {
    val vm: VaultViewModel = viewModel()
    val history by vm.history.collectAsState()
    val settingsOrNull by vm.settings.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val settings = settingsOrNull
        if (settings == null) {
            return@Box
        }

        val startDestination = remember {
            if (settings.splashEnabled) "splash" else "dashboard"
        }
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        ) {
            composable("splash") {
                SplashScreen(onFinished = {
                    navController.navigate("dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }

            composable("dashboard") {
                DashboardScreen(
                    vm = vm,
                    onInitiate = {
                        vm.startBreach()
                        navController.navigate("breach")
                    },
                    onHistory = { navController.navigate("history") },
                    onSettings = { navController.navigate("settings") }
                )
            }

            composable("breach") {
                BreachScreen(
                    vm = vm,
                    onFinished = { recordId ->
                        navController.navigate("receipt/$recordId") {
                            popUpTo("breach") { inclusive = true }
                        }
                    },
                    onAbort = {
                        vm.abortBreach()
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "receipt/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id").orEmpty()
                val record = history.firstOrNull { it.id == id }
                if (record == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RECEIPT NOT FOUND",
                            color = DimText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    ReceiptScreen(
                        record = record,
                        onViewLog = { navController.navigate("history") },
                        onNewBreach = {
                            val popped = navController.popBackStack("dashboard", inclusive = false)
                            if (!popped) {
                                navController.navigate("dashboard") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }

            composable("history") {
                HistoryScreen(
                    history = history,
                    onOpen = { id -> navController.navigate("receipt/$id") },
                    onDelete = { id -> vm.deleteRecord(id) },
                    onClearAll = { vm.clearHistory() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(
                    settings = settings,
                    onToggleSplash = { vm.setSplashEnabled(it) },
                    onToggleSound = { vm.setSoundEnabled(it) },
                    onShredLogs = { vm.clearHistory() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
