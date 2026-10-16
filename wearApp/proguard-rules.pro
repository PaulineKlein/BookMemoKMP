# App & Domain Model classes
-keep class com.pklein.bookmemokmp.wear.** { *; }
-keep class com.pklein.bookmemokmp.domain.model.** { *; }

# Keep enum values (used by ItemType.valueOf)
-keepclassmembers enum * { public static **[] values(); public static ** valueOf(java.lang.String); }

# Ignore warnings for JUnit and Android test classes referenced in library dependencies during R8 processing
-dontwarn android.test.**
-dontwarn junit.**

# Koin
-keep class org.koin.** { *; }
-keepnames class * implements org.koin.core.module.Module


