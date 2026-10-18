package com.animeblack.app.data.repository

import com.animeblack.app.data.model.Anime
import com.animeblack.app.data.model.Episode
import com.animeblack.app.data.network.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnimeRepository(
    private val apiClient: ApiClient = ApiClient()
) {
    private val defaultAnimeList = listOf(
        Anime(
            id = "a1",
            title = "Jujutsu Kaisen Season 2",
            titleAr = "جوجوتسو كايسن الموسم الثاني",
            poster = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
            cover = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1200&auto=format&fit=crop&q=80",
            score = 9.1,
            type = "TV",
            status = "مكتمل",
            year = 2023,
            season = "خريف 2023",
            episodesCount = 23,
            genres = listOf("أكشن", "خوارق", "شياطين", "شونين"),
            synopsis = "تدور أحداث الموسم الثاني حول ماضي غوجو غيتو وحادثة شيبويا الملحمية التي غيرت مجرى عالم الجوجوتسو إلى الأبد.",
            studio = "MAPPA",
            episodes = listOf(
                Episode(
                    id = "ep1",
                    number = 1,
                    title = "المخزون المخفي",
                    duration = "23:45",
                    thumbnail = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80",
                    streamUrls = mapOf(
                        "1080p (FHD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        "720p (HD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                        "HLS Live" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
                    ),
                    defaultUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                ),
                Episode(
                    id = "ep2",
                    number = 2,
                    title = "الموت المبكر",
                    duration = "24:10",
                    thumbnail = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80",
                    streamUrls = mapOf(
                        "1080p (FHD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                        "720p (HD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                    ),
                    defaultUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
                )
            )
        ),
        Anime(
            id = "a2",
            title = "Attack on Titan: The Final Season",
            titleAr = "هجوم العمالقة: الموسم الأخير",
            poster = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
            cover = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            score = 9.4,
            type = "TV",
            status = "مكتمل",
            year = 2023,
            season = "شتاء 2023",
            episodesCount = 28,
            genres = listOf("أكشن", "دراما", "خيال عسكري", "غموض"),
            synopsis = "المعركة الختامية بين جزيرة باراديس وإمبراطورية مارلي، حيث تتصادم الدوافع لتحقيق الحرية بأي ثمن.",
            studio = "MAPPA",
            episodes = listOf(
                Episode(
                    id = "aot_ep1",
                    number = 1,
                    title = "الجانب الآخر من المحيط",
                    duration = "23:50",
                    thumbnail = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
                    streamUrls = mapOf(
                        "1080p (FHD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                        "720p (HD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
                    ),
                    defaultUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
                )
            )
        ),
        Anime(
            id = "a3",
            title = "Solo Leveling",
            titleAr = "سولو ليفلينج (ارتقاء منفرد)",
            poster = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600&auto=format&fit=crop&q=80",
            cover = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
            score = 8.8,
            type = "TV",
            status = "مستمر",
            year = 2024,
            season = "شتاء 2024",
            episodesCount = 12,
            genres = listOf("أكشن", "فانتازيا", "مغامرات"),
            synopsis = "سونغ جين وو، أضعف صياد في البشرية، يكتسب قدرة غامضة تمكنه وحده من ترقية مستواه ومواجهة أخطر البوابات.",
            studio = "A-1 Pictures",
            episodes = listOf(
                Episode(
                    id = "sl_ep1",
                    number = 1,
                    title = "أنا معتاد على ذلك",
                    duration = "24:00",
                    thumbnail = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600&auto=format&fit=crop&q=80",
                    streamUrls = mapOf(
                        "1080p (FHD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                        "HLS Stream" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
                    ),
                    defaultUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
                )
            )
        )
    )

    private var cachedList: List<Anime> = defaultAnimeList

    fun getTrendingAnime(): Flow<List<Anime>> = flow {
        // First emit cached default list
        emit(cachedList)

        // Then try fetching live from backend server
        val serverList = apiClient.fetchTrendingAnime()
        if (serverList.isNotEmpty()) {
            cachedList = serverList
            emit(cachedList)
        }
    }

    fun getAnimeById(id: String): Anime? {
        return cachedList.find { it.id == id } ?: defaultAnimeList.find { it.id == id }
    }
}
