package com.animeblack.app.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "الرئيسية")
    object Reels : Screen("reels", "ريلز")
    object Manga : Screen("manga", "المانجا")
    object Community : Screen("community", "المجتمع")
    object Chat : Screen("chat", "المحادثات")
    object Store : Screen("store", "المتجر")
    object Profile : Screen("profile", "حسابي")
    object Library : Screen("library", "المكتبة")
    object Notifications : Screen("notifications", "الإشعارات")
    object Settings : Screen("settings", "الإعدادات")
    object Login : Screen("login", "تسجيل الدخول")
    object Player : Screen("player/{animeId}/{episodeId}", "المشغل") {
        fun createRoute(animeId: String, episodeId: String) = "player/$animeId/$episodeId"
    }
}
