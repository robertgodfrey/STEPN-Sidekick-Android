-keepattributes *Annotation*

# Preserves the line number information for
# debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hides the original source file name
-renamesourcefileattribute SourceFile

# Retrofit
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# GSON
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep annotation
-keep @androidx.annotation.Keep class * { *; }
