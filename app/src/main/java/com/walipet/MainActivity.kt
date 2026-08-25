package com.walipet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.walipet.di.ServiceLocator
import com.walipet.engine.model.Rule
import com.walipet.ui.codex.CodexScreen
import com.walipet.ui.dashboard.DashboardScreen
import com.walipet.ui.onboarding.OnboardingScreen
import com.walipet.ui.rules.RuleEditorScreen
import com.walipet.ui.rules.RulesScreen
import com.walipet.ui.stats.StatsScreen
import com.walipet.ui.theme.ArcadeSecondary
import com.walipet.ui.theme.ArcadeSurface
import com.walipet.ui.theme.ArcadeSurfaceVariant
import com.walipet.ui.theme.WaliPetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaliPetTheme {
                WaliPetRoot()
            }
        }
    }
}

/**
 * Raíz de la app: decide entre onboarding (primera vez) y la app principal,
 * y aloja el editor de reglas como overlay global.
 */
@Composable
private fun WaliPetRoot() {
    val context = LocalContext.current
    val serviceLocator = remember { ServiceLocator.getInstance(context.applicationContext) }
    val settings = serviceLocator.settingsRepository

    var onboardingDone by remember { mutableStateOf(settings.onboardingCompleted) }
    var editingRule by remember { mutableStateOf<Rule?>(null) }

    if (!onboardingDone) {
        OnboardingScreen(
            onFinished = {
                settings.onboardingCompleted = true
                onboardingDone = true
            },
        )
        return
    }

    val ruleRepository = serviceLocator.ruleRepository
    val scope = rememberCoroutineScope()

    WaliPetScaffold(
        onNewRule = { editingRule = Rule.blank() },
        onEditRule = { editingRule = it },
    )

    editingRule?.let { rule ->
        RuleEditorScreen(
            rule = rule,
            onSave = { saved ->
                scope.launch { ruleRepository.save(saved) }
                editingRule = null
            },
            onDelete = { toDelete ->
                scope.launch { ruleRepository.delete(toDelete) }
                editingRule = null
            },
            onDismiss = { editingRule = null },
        )
    }
}

/** Destinos de la barra de navegación inferior. */
private data class Tab(val route: String, val labelRes: Int, val icon: ImageVector)

private val tabs = listOf(
    Tab("habitat", R.string.nav_habitat, Icons.Filled.Pets),
    Tab("reglas", R.string.nav_rules, Icons.Filled.Rule),
    Tab("codex", R.string.nav_codex, Icons.Filled.AutoAwesome),
    Tab("stats", R.string.nav_stats, Icons.Filled.Leaderboard),
)

@Composable
private fun WaliPetScaffold(
    onNewRule: () -> Unit,
    onEditRule: (Rule) -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = ArcadeSurface) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                        label = { Text(text = stringResource(tab.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ArcadeSecondary,
                            selectedTextColor = ArcadeSecondary,
                            indicatorColor = ArcadeSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == "habitat" || currentRoute == "reglas") {
                FloatingActionButton(
                    onClick = onNewRule,
                    containerColor = ArcadeSecondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.dashboard_new_rule))
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            NavHost(navController = navController, startDestination = "habitat") {
                composable("habitat") { DashboardScreen(onNewRule = onNewRule) }
                composable("reglas") { RulesScreen(onNewRule = onNewRule, onEditRule = onEditRule) }
                composable("codex") { CodexScreen() }
                composable("stats") { StatsScreen() }
            }
        }
    }
}
