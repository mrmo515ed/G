package com.animeblack.app.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@OptIn(UnstableApi::class)
object AnimeCacheManager {
    private var simpleCache: SimpleCache? = null
    private var databaseProvider: StandaloneDatabaseProvider? = null

    // 1GB offline cache size for anime episodes and clips
    private const val MAX_CACHE_BYTES: Long = 1024 * 1024 * 1024

    @Synchronized
    fun getSimpleCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheDir = File(context.cacheDir, "anime_media_cache")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES)
            databaseProvider = StandaloneDatabaseProvider(context)
            simpleCache = SimpleCache(cacheDir, evictor, databaseProvider!!)
        }
        return simpleCache!!
    }

    @Synchronized
    fun createCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val cache = getSimpleCache(context)
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setUserAgent("AnimeBlack/1.0.0 (Android)")

        val upstreamFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun isCached(context: Context, url: String): Boolean {
        val cache = getSimpleCache(context)
        val uri = android.net.Uri.parse(url)
        val keys = cache.keys
        return keys.contains(uri.toString())
    }

    fun clearCache(context: Context) {
        try {
            getSimpleCache(context).keys.forEach { key ->
                getSimpleCache(context).removeResource(key)
            }
        } catch (_: Exception) {}
    }
}
