package com.animeblack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.animeblack.app.data.repository.AnimeRepository
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.navigation.Screen
import com.animeblack.app.ui.screens.chat.ChatScreen
import com.animeblack.app.ui.screens.community.CommunityScreen
import com.animeblack.app.ui.screens.home.HomeScreen
import com.animeblack.app.ui.screens.manga.MangaScreen
import com.animeblack.app.ui.screens.player.PlayerScreen
import com.animeblack.app.ui.screens.profile.ProfileScreen
import com.animeblack.app.ui.screens.reels.ReelsScreen
import com.animeblack.app.ui.screens.store.StoreScreen
import com.animeblack.app.ui.theme.AccentRed
import com.animeblack.app.ui.theme.AnimeBlackTheme
import com.animeblack.app.ui.theme.DarkCard
import com.animeblack.app.ui.theme.TextMuted

class MainActivity : ComponentActivity() {
    private val firebaseRepository = FirebaseRepository()
    private val animeRepository = AnimeRepository()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimeBlackTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavItems = listOf(
                    Triple(Screen.Home, Icons.Default.Home, "الرئيسية"),
                    Triple(Screen.Reels, Icons.Default.VideoLibrary, "ريلز"),
                    Triple(Screen.Manga, Icons.Default.MenuBook, "المانجا"),
                    Triple(Screen.Community, Icons.Default.Groups, "المجتمع"),
                    Triple(Screen.Profile, Icons.Default.Person, "حسابي")
                )

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Reels.route,
                    Screen.Manga.route,
                    Screen.Community.route,
                    Screen.Profile.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = DarkCard
                            ) {
                                bottomNavItems.forEach { (screen, icon, label) ->
                                    val isSelected = currentRoute == screen.route
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Home.route) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = AccentRed,
                                            selectedTextColor = AccentRed,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted,
                                            indicatorColor = AccentRed.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                animeRepository = animeRepository,
                                onAnimeClick = { animeId ->
                                    val anime = animeRepository.getAnimeById(animeId)
                                    val firstEpId = anime?.episodes?.firstOrNull()?.id ?: "ep1"
                                    navController.navigate(Screen.Player.createRoute(animeId, firstEpId))
                                },
                                onEpisodeClick = { animeId, epId ->
                                    navController.navigate(Screen.Player.createRoute(animeId, epId))
                                }
                            )
                        }

                        composable(Screen.Reels.route) {
                            ReelsScreen()
                        }

                        composable(Screen.Manga.route) {
                            MangaScreen(onBack = { navController.popBackStack() })
                        }

                        composable(Screen.Community.route) {
                            CommunityScreen(firebaseRepository = firebaseRepository)
                        }

                        composable(Screen.Chat.route) {
                            ChatScreen(firebaseRepository = firebaseRepository)
                        }

                        composable(Screen.Store.route) {
                            StoreScreen()
                        }

                        composable(Screen.Profile.route) {
                            ProfileScreen(firebaseRepository = firebaseRepository)
                        }

                        composable(
                            route = Screen.Player.route,
                            arguments = listOf(
                                navArgument("animeId") { type = NavType.StringType },
                                navArgument("episodeId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
                            val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""
                            PlayerScreen(
                                animeId = animeId,
                                episodeId = episodeId,
                                animeRepository = animeRepository,
                                onBack = { navController.popBackStack() },
                                onSelectEpisode = { newEpId ->
                                    navController.navigate(Screen.Player.createRoute(animeId, newEpId)) {
                                        popUpTo(Screen.Player.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
