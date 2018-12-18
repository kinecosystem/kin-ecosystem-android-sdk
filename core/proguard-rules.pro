##---------------Begin: proguard configuration for Gson  ----------
# Took the congif from: https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Application classes that will be serialized/deserialized over Gson
-keepclassmembers enum com.kin.ecosystem.core.** { *; }
-keep class com.kin.ecosystem.core.bi.** { *; }
-keep class com.kin.ecosystem.core.network.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------
