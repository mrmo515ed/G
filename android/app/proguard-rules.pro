# Anime Black Proguard Rules

# Keep Models for Firebase Deserialization
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class com.animeblack.app.data.model.** { *; }

# Media3 ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**
