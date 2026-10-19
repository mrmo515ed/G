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
import com.animeblack.app.ui.screens.auth.LoginScreen
import com.animeblack.app.ui.screens.chat.ChatScreen
import com.animeblack.app.ui.screens.community.CommunityScreen
import com.animeblack.app.ui.screens.home.HomeScreen
import com.animeblack.app.ui.screens.library.LibraryScreen
import com.animeblack.app.ui.screens.manga.MangaScreen
import com.animeblack.app.ui.screens.notifications.NotificationsScreen
import com.animeblack.app.ui.screens.player.PlayerScreen
import com.animeblack.app.ui.screens.profile.ProfileScreen
import com.animeblack.app.ui.screens.reels.ReelsScreen
import com.animeblack.app.ui.screens.settings.SettingsScreen
import com.animeblack.app.ui.screens.store.StoreScreen
import com.animeblack.app.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
                                containerColor = DarkCharcoal,
                                tonalElevation = Dimensions.spaceNone
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
                                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = CyberBlue,
                                            selectedTextColor = CyberBlue,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted,
                                            indicatorColor = CyberBlueMuted
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
                                },
                                onNotificationsClick = {
                                    navController.navigate(Screen.Notifications.route)
                                },
                                onLibraryClick = {
                                    navController.navigate(Screen.Library.route)
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
                            ProfileScreen(
                                firebaseRepository = firebaseRepository,
                                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                                onLibraryClick = { navController.navigate(Screen.Library.route) },
                                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
                            )
                        }

                        composable(Screen.Library.route) {
                            LibraryScreen(
                                animeRepository = animeRepository,
                                firebaseRepository = firebaseRepository,
                                onAnimeClick = { animeId ->
                                    val anime = animeRepository.getAnimeById(animeId)
                                    val firstEpId = anime?.episodes?.firstOrNull()?.id ?: "ep1"
                                    navController.navigate(Screen.Player.createRoute(animeId, firstEpId))
                                },
                                onPlayEpisode = { animeId, epId ->
                                    navController.navigate(Screen.Player.createRoute(animeId, epId))
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Notifications.route) {
                            NotificationsScreen(
                                firebaseRepository = firebaseRepository,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                firebaseRepository = firebaseRepository,
                                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Login.route) {
                            LoginScreen(
                                firebaseRepository = firebaseRepository,
                                onLoginSuccess = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
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
