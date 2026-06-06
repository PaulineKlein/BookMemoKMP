# Kotlin
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

# Kotlin serialization
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-keep,includedescriptorclasses class com.pklein.bookmemokmp.**$$serializer { *; }
-keepclassmembers class com.pklein.bookmemokmp.** {
    *** Companion;
}
-keepclasseswithmembers class com.pklein.bookmemokmp.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Koin
-keep class org.koin.** { *; }
-keepnames class * implements org.koin.core.module.Module

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.ktor.**

# SQLDelight
-keep class app.cash.sqldelight.** { *; }
-keep class com.pklein.bookmemokmp.database.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Glance widget
-keep class androidx.glance.** { *; }
-dontwarn androidx.glance.**

# App classes
-keep class com.pklein.bookmemokmp.** { *; }

# Keep enum values (used by ItemType.fromString)
-keepclassmembers enum * { public static **[] values(); public static ** valueOf(java.lang.String); }

# OkHttp (used by Ktor on Android)
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }