# Readable crash stack traces (Crashlytics / Play Console use the mapping file)
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin serialization: only the @Serializable DTOs used by Ktor JSON need their
# generated serializers found reflectively (serializer(KType) -> Companion.serializer()).
-keepattributes *Annotation*, InnerClasses, RuntimeVisibleAnnotations, AnnotationDefault
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.pklein.bookmemokmp.data.remote.dto.**$$serializer { *; }
-keepclassmembers class com.pklein.bookmemokmp.data.remote.dto.** {
    *** Companion;
}
-keepclasseswithmembers class com.pklein.bookmemokmp.data.remote.dto.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor: HttpClient {} without an explicit engine discovers it through ServiceLoader
-keep class * implements io.ktor.client.engine.HttpClientEngineContainer { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.ktor.**

# Keep enum values (used by ItemType.fromString)
-keepclassmembers enum * { public static **[] values(); public static ** valueOf(java.lang.String); }

# OkHttp (used by Ktor on Android) ships its own consumer rules
-dontwarn okhttp3.**
-dontwarn okio.**
