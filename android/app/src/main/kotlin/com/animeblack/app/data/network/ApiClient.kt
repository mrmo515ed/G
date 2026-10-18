package com.animeblack.app.data.network

import com.animeblack.app.data.model.Anime
import com.animeblack.app.data.model.Episode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ApiClient(
    private val baseUrl: String = "https://ais-dev-qk6u76au77qi5ssfi2wrxr-843786932845.europe-west2.run.app"
) {
    // 1. Check Backend Server Health
    suspend fun checkServerHealth(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/health")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val responseCode = conn.responseCode
            responseCode == 200
        } catch (e: Exception) {
            false
        }
    }

    // 2. Fetch Live Trending Anime from Backend Server
    suspend fun fetchTrendingAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/anime/trending")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                if (json.optBoolean("success", false)) {
                    val data = json.getJSONArray("data")
                    val list = mutableListOf<Anime>()
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        list.add(
                            Anime(
                                id = item.optString("id", "ani_$i"),
                                title = item.optString("title", ""),
                                titleAr = item.optString("titleAr", item.optString("title", "")),
                                poster = item.optString("poster", ""),
                                cover = item.optString("cover", item.optString("poster", "")),
                                score = item.optDouble("score", 8.5),
                                type = item.optString("type", "TV"),
                                status = item.optString("status", "مستمر"),
                                year = item.optInt("year", 2024),
                                season = item.optString("season", "2024"),
                                episodesCount = item.optInt("episodesCount", 12),
                                synopsis = item.optString("synopsis", ""),
                                studio = item.optString("studio", ""),
                                episodes = listOf(
                                    Episode(
                                        id = "ep_1",
                                        number = 1,
                                        title = "الحلقة الأولى",
                                        streamUrls = mapOf(
                                            "1080p (FHD)" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                                            "HLS Live" to "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
                                        ),
                                        defaultUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                                    )
                                )
                            )
                        )
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            // Fallback to local catalog if server is unreachable
        }
        emptyList()
    }

    // 3. Query Gemini Search Agent on Backend
    suspend fun askGeminiAgent(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/api/gemini/search-agent")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 10000
            conn.readTimeout = 15000

            val body = JSONObject().apply {
                put("prompt", prompt)
                put("mode", "general")
            }.toString()

            conn.outputStream.use { os ->
                os.write(body.toByteArray())
            }

            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val resp = reader.readText()
                reader.close()
                val json = JSONObject(resp)
                return@withContext json.optString("text", "لا توجد إجابة")
            }
        } catch (e: Exception) {
            return@withContext "تعذر الاتصال بخادم الذكاء الاصطناعي: ${e.message}"
        }
        "تعذر الحصول على رد من الخادم"
    }
}
